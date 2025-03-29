#include "kernel.h"

#include <stdint.h>
#include <stdbool.h>
#include <stdint.h>

#define UART_BASE 0x10000000  // Базовый адрес UART в QEMU
#define UART_LSR (UART_BASE + 5)  // Line Status Register
#define UART_RBR (UART_BASE + 0)  // Receiver Buffer Register
#define UART_THR (UART_BASE + 0)  // Transmit Holding Register

#define UART_LSR_DATA_READY 0x01  // Флаг наличия данных
#define UART_LSR_TX_EMPTY 0x20    // Флаг готовности к передаче

#define MAX_CMD_LENGTH 128  // Максимальная длина команды

#define SBI_EXT_BASE 0x10
#define SBI_EXT_HSM  0x48534D
#define SBI_EXT_SRST 0x53525354

#define SBI_SUCCESS  0
#define SBI_ERR_FAILED -1

typedef unsigned char uint8_t;
typedef unsigned int uint32_t;
typedef uint32_t size_t;

extern char __bss[], __bss_end[], __stack_top[];

// SBI вызов (inline assembly)
struct sbiret sbi_call(long arg0, long arg1, long arg2, long arg3, long arg4,
                       long arg5, long fid, long eid) {
    register long a0 __asm__("a0") = arg0;
    register long a1 __asm__("a1") = arg1;
    register long a2 __asm__("a2") = arg2;
    register long a3 __asm__("a3") = arg3;
    register long a4 __asm__("a4") = arg4;
    register long a5 __asm__("a5") = arg5;
    register long a6 __asm__("a6") = fid;
    register long a7 __asm__("a7") = eid;

    __asm__ __volatile__("ecall"
                         : "=r"(a0), "=r"(a1)
                         : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(a4), "r"(a5),
                           "r"(a6), "r"(a7)
                         : "memory");
    return (struct sbiret){.error = a0, .value = a1};
}

int strcmp(const char *s1, const char *s2) {
    while (*s1 && (*s1 == *s2)) {
        s1++;
        s2++;
    }
    return *(const unsigned char *)s1 - *(const unsigned char *)s2;
}

int strncmp(const char *s1, const char *s2, size_t n) {
    while (n && *s1 && (*s1 == *s2)) {
        s1++;
        s2++;
        n--;
    }
    return n == 0 ? 0 : (*(unsigned char *)s1 - *(unsigned char *)s2);
}

// Получение версии OpenSBI
uint32_t sbi_get_spec_version() {
    struct sbiret ret = sbi_call(0, 0, 0, 0, 0, 0, 0, SBI_EXT_BASE);
    return ret.value;  // Версия возвращается в value
}

// Получение статуса hart (ядра)
long sbi_hart_status(unsigned long hartid) {
    struct sbiret ret = sbi_call(hartid, 0, 0, 0, 0, 0, 2, SBI_EXT_HSM);
    return ret.value;  // Статус ядра в value
}

// Остановка текущего hart
void sbi_hart_stop() {
    sbi_call(0, 0, 0, 0, 0, 0, 1, SBI_EXT_HSM);
}

// Выключение системы
void sbi_shutdown() {
    sbi_call(0, 0, 0, 0, 0, 0, 0, SBI_EXT_SRST);
}


// Функция для чтения символа из UART
char getchar() {
    while (!(*(volatile uint8_t *)UART_LSR & UART_LSR_DATA_READY));
    return *(volatile uint8_t *)UART_RBR;
}

// Функция для вывода символа в UART
void putchar(char c) {
    while (!(*(volatile uint8_t *)UART_LSR & UART_LSR_TX_EMPTY));
    *(volatile uint8_t *)UART_THR = c;
}

// Функция для вывода строки
void puts(const char *str) {
    while (*str) {
        putchar(*str++);
    }
}

// Функция обработки команды
void handle_command(const char *cmd) {
    if (strcmp(cmd, "get_version") == 0) {
        uint32_t version = sbi_get_spec_version();
        puts("SBI Version: ");
        putchar('0' + ((version >> 24) & 0xF));  // Основная версия
        puts(".");
        putchar('0' + ((version >> 16) & 0xF));  // Минорная версия
        puts("\n");
    } else if (strncmp(cmd, "hart_status ", 12) == 0) {
        int hart_id = cmd[12] - '0';  // Простая обработка числа
        long status = sbi_hart_status(hart_id);
        puts("Hart ");
        putchar('0' + hart_id);
        puts(" status: ");
        putchar('0' + (status & 0xF));
        puts("\n");
    } else if (strcmp(cmd, "hart_stop") == 0) {
        puts("Stopping current hart...\n");
        sbi_hart_stop();
    } else if (strcmp(cmd, "shutdown") == 0) {
        puts("Shutting down system...\n");
        sbi_shutdown();
    } else {
        puts("Unknown command!\n");
    }
}


// Функция чтения строки из UART
void readline(char *buffer, int max_length) {
    int i = 0;
    while (i < max_length - 1) {
        char c = getchar();
        if (c == '\r' || c == '\n') {  // Конец ввода
            putchar('\n');
            buffer[i] = '\0';
            return;
        } else if (c == '\b' && i > 0) {  // Обработка backspace
            putchar('\b');
            putchar(' ');
            putchar('\b');
            i--;
        } else {
            putchar(c);  // Эхо ввода
            buffer[i++] = c;
        }
    }
    buffer[i] = '\0';  // Завершаем строку
}

void kernel_main(void) {
    puts("Welcome to my OS!\n");

    char cmd[MAX_CMD_LENGTH];

    for (;;) {
        puts("> ");
        readline(cmd, MAX_CMD_LENGTH);
        handle_command(cmd);
    }
}

__attribute__((section(".text.boot")))
__attribute__((naked))
void boot(void) {
    __asm__ __volatile__(
        "mv sp, %[stack_top]\n" // Устанавливаем указатель стека
        "j kernel_main\n"       // Переходим к функции main ядра
        :
        : [stack_top] "r" (__stack_top) // Передаём верхний адрес стека в виде %[stack_top]
    );
}