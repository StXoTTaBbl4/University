#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdint.h>
#include <stdlib.h>
#include <elf.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>

// Function prototypes
//# 1
int parse_arguments(int argc, char *argv[], char **elf_filename, char **section_name);
//# 2
int open_elf_file(const char *filename, int *fd);
int read_elf_header(int fd, Elf64_Ehdr *header);
int check_elf_magic(const Elf64_Ehdr *header);
//# 3
int load_section(int fd, const Elf64_Ehdr *elf_header, const char *section_name, void **section_start_addr);
int find_section(int fd, const Elf64_Ehdr *elf_header, const char *section_name, Elf64_Shdr *section_header);
//# 4
int transfer_control(void *section_start_address);

int main(int argc, char *argv[]) {
    char *elf_filename = NULL;
    char *section_name = NULL;

    int result = parse_arguments(argc, argv, &elf_filename, &section_name);
    if (result != 0) {
        return result; // error code
    }

    write(2, "Arguments parsed successfully\n", 30);

    int fd;
    result = open_elf_file(elf_filename, &fd);
    if (result != 0) {
        return result;
    }

    Elf64_Ehdr elf_header;
    result = read_elf_header(fd, &elf_header);
    if (result != 0) {
        close(fd);
        return result;
    }

    result = check_elf_magic(&elf_header);
    if (result != 0) {
        close(fd);
        return result;
    }

    write(2, "ELF header read successfully\n", 29);

    void *section_start_addr = NULL;
    result = load_section(fd, &elf_header, section_name, &section_start_addr);
    if (result != 0) {
        close(fd);
        return result;
    }

    close(fd);

    result = transfer_control(section_start_addr);
    if (result != 0) {
        write(2, "Error: Control transfer failed\n", 30);
    }

    return result;

}

/**
 * Parse command-line arguments and validate.
 * @param argc The argument count.
 * @param argv The argument vector.
 * @param elf_filename Pointer to store the ELF filename.
 * @param section_name Pointer to store the section name.
 * @return 0 on success, EINVAL on error.
 */
int parse_arguments(int argc, char *argv[], char **elf_filename, char **section_name) {
    // Ensure exactly two arguments are passed for <source-elf64-file> and <section-name>
    if (argc != 3) {
        write(2, "Error: Incorrect number of arguments\n", 37);
        return EINVAL;
    }

    *elf_filename = argv[1];
    *section_name = argv[2];

    if (*elf_filename[0] == '\0' || *section_name[0] == '\0') {
        write(2, "Error: Empty argument provided\n", 31);
        return EINVAL;
    }

    return 0;
}

/**
 * Open the ELF file with the specified filename.
 * @param filename The name of the ELF file to open.
 * @param fd Pointer to store the file descriptor.
 * @return 0 on success, ENOENT if the file can't be opened.
 */
int open_elf_file(const char *filename, int *fd) {
    *fd = open(filename, O_RDONLY);
    if (*fd < 0) {
        write(2, "Error: Failed to open file\n", 27);
        return ENOENT;
    }
    return 0;
}

/**
 * Read the ELF header from the file descriptor.
 * @param fd The file descriptor of the ELF file.
 * @param header Pointer to store the ELF header.
 * @return 0 on success, EIO if the read fails.
 */
int read_elf_header(int fd, Elf64_Ehdr *header) {
    ssize_t bytes_read = read(fd, header, sizeof(Elf64_Ehdr));
    if (bytes_read != sizeof(Elf64_Ehdr)) {
        write(2, "Error: Failed to read ELF header\n", 33);
        return EIO;
    }
    return 0;
}

/**
 * Check if the ELF header has a valid magic number.
 * @param header Pointer to the ELF header to check.
 * @return 0 on success if the magic number is correct, or EINVAL otherwise.
 */
int check_elf_magic(const Elf64_Ehdr *header) {
    if (header->e_ident[EI_MAG0] != 0x7f ||
        header->e_ident[EI_MAG1] != 'E' ||
        header->e_ident[EI_MAG2] != 'L' ||
        header->e_ident[EI_MAG3] != 'F') {
        write(2, "Error: Invalid ELF magic number\n", 32);
        return EINVAL;
        }
    return 0;
}

/**
 * Load the specified section into memory.
 * @param fd The file descriptor of the ELF file.
 * @param elf_header Pointer to the ELF header.
 * @param section_name The name of the section to load.
 * @param section_start_addr The pointer to start of the section
 * @return 0 on success, error code on failure.
 */
int load_section(const int fd, const Elf64_Ehdr *elf_header, const char *section_name, void **section_start_addr) {
    Elf64_Shdr section_header;
    const int result = find_section(fd, elf_header, section_name, &section_header);
    if (result != 0) {
        return result;
    }

    if (!(section_header.sh_flags & SHF_EXECINSTR)) {
        write(2, "Error: Section is not executable\n", 32);
        return EACCES;
    }

    *section_start_addr = (void *)section_header.sh_addr;

    const size_t page_size = sysconf(_SC_PAGESIZE);
    const Elf64_Addr aligned_addr = section_header.sh_addr & ~(page_size - 1);
    // size_t offset = section_header.sh_offset - (section_header.sh_addr - aligned_addr);
    const size_t size = section_header.sh_size + (section_header.sh_addr - aligned_addr);

    void *mapped_section = mmap((void *)aligned_addr, size, PROT_READ | PROT_WRITE | PROT_EXEC,
                                MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (mapped_section == MAP_FAILED) {
        write(2, "Error: Memory mapping failed\n", 29);
        return ENOMEM;
    }

    if (lseek(fd, section_header.sh_offset, SEEK_SET) == -1) {
        write(2, "Error: Failed to seek to section offset\n", 40);
        munmap(mapped_section, size);
        return EIO;
    }
    if (read(fd, mapped_section, section_header.sh_size) != section_header.sh_size) {
        write(2, "Error: Failed to read section data\n", 34);
        munmap(mapped_section, size);
        return EIO;
    }

    int prot = 0;
    if (section_header.sh_flags & SHF_EXECINSTR) prot |= PROT_EXEC;
    if (section_header.sh_flags & SHF_WRITE) prot |= PROT_WRITE;
    if (section_header.sh_flags & SHF_ALLOC) prot |= PROT_READ;

    if (mprotect(mapped_section, size, prot) != 0) {
        write(2, "Error: Failed to set memory protection\n", 38);
        munmap(mapped_section, size);
        return EACCES;
    }

    write(2, "Section loaded successfully\n", 27);
    return 0;
}

/**
 * Locate the specified section by name and populate the section header.
 * @param fd The file descriptor of the ELF file.
 * @param elf_header Pointer to the ELF header.
 * @param section_name The name of the section to find.
 * @param section_header Pointer to store the section header of the located section.
 * @return 0 on success, EINVAL if the section is not found or inaccessible.
 */
int find_section(const int fd, const Elf64_Ehdr *elf_header, const char *section_name, Elf64_Shdr *section_header) {
    Elf64_Shdr shstrtab_header;
    if (lseek(fd, elf_header->e_shoff + elf_header->e_shstrndx * sizeof(Elf64_Shdr), SEEK_SET) == -1) {
        write(2, "Error: Failed to seek to section string table\n", 46);
        return EIO;
    }
    if (read(fd, &shstrtab_header, sizeof(Elf64_Shdr)) != sizeof(Elf64_Shdr)) {
        write(2, "Error: Failed to read section string table header\n", 50);
        return EIO;
    }
    for (int i = 0; i < elf_header->e_shnum; i++) {
        if (lseek(fd, elf_header->e_shoff + i * sizeof(Elf64_Shdr), SEEK_SET) == -1) {
            write(2, "Error: Failed to seek to section header\n", 40);
            return EIO;
        }
        if (read(fd, section_header, sizeof(Elf64_Shdr)) != sizeof(Elf64_Shdr)) {
            write(2, "Error: Failed to read section header\n", 36);
            return EIO;
        }

        char name[256];
        if (lseek(fd, shstrtab_header.sh_offset + section_header->sh_name, SEEK_SET) == -1) {
            write(2, "Error: Failed to seek to section name\n", 38);
            return EIO;
        }
        if (read(fd, name, sizeof(name) - 1) <= 0) {
            write(2, "Error: Failed to read section name\n", 35);
            return EIO;
        }
        name[sizeof(name) - 1] = '\0';

        if (!strcmp(name, section_name)) {
            write(2, "Section found:\n",15);
            write(2, name ,strlen(name));
            write(2, "\n" ,1);
            return 0;
        }
    }

    write(2, "Error: Specified section not found or inaccessible\n", 52);
    return EINVAL;
}

/**
 * Temporary function to write entry point to section
 * @param entry_point Address of the beginning of the section
 */
void log_entry_point(void (*entry_point)(void)) {
    char buffer[20];
    const uintptr_t address = (uintptr_t)entry_point;
    const int size = snprintf(buffer, sizeof(buffer), "%#lx\n", address);
    write(2, "Beginning of section:\n", 23);
    write(2, buffer, size);
}


/**
 * Transfer control to the specified section's start address.
 * @param section_start_address The starting address of the section.
 * @return 0 on successful transfer of control, EINVAL if the address is invalid.
 */
int transfer_control(void *section_start_address) {
    if (section_start_address == NULL) {
        write(2, "Error: Invalid section start address\n", 36);
        return EINVAL;
    }

    // Define a function pointer to jump to the section's address
    // void (*entry_point)(void) = (void (*)(void)) section_start_address;
    void (*entry_point)(void) = (void (*)(void))(uintptr_t)section_start_address;

    log_entry_point(entry_point);

    entry_point();

    write(2, "Warning: Returned unexpectedly from section\n", 44);
    return 0;
}
