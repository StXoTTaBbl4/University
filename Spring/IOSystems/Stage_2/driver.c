#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/uaccess.h>
#include <linux/cdev.h>
#include <linux/slab.h>

#define DEVICE_NAME "var4"
#define CLASS_NAME "var4_class"

static dev_t dev_number;
static struct cdev cdev;
static struct class *cls;
static int digit_count = 0;
static char results[1024];
static size_t results_len = 0;

// чтение
static ssize_t dev_read(struct file *file, char __user *buf, size_t count, loff_t *ppos) {
    if (*ppos >= results_len) return 0; 
    if (count > results_len - *ppos) count = results_len - *ppos;
    if (copy_to_user(buf, results + *ppos, count)) return -EFAULT;
    *ppos += count;
    return count;
}

// запись 
static ssize_t dev_write(struct file *file, const char __user *buf, size_t count, loff_t *ppos) {
    char *kbuf;
    int i;
    int local_digit_count = 0;
    
    kbuf = kmalloc(count + 1, GFP_KERNEL); // память
    if (!kbuf) return -ENOMEM;
    
    if (copy_from_user(kbuf, buf, count)) { // данные в ядро
        kfree(kbuf);
        return -EFAULT;
    }
    kbuf[count] = '\0';
    
    for (i = 0; i < count; i++) { // считаем цифры
        if (kbuf[i] >= '0' && kbuf[i] <= '9') {
            local_digit_count++;
        }
    }
    
    digit_count += local_digit_count;
    results_len += snprintf(results + results_len, sizeof(results) - results_len, "%d\n", local_digit_count); // сохранение 
    
    kfree(kbuf);
    return count;
}

static struct file_operations fops = {
    .owner = THIS_MODULE,
    .read = dev_read,
    .write = dev_write,
};

// инициализация и наоборот
static int __init dev_init(void) {
    if (alloc_chrdev_region(&dev_number, 0, 1, DEVICE_NAME) < 0) {
        return -1;
    }
    
    cdev_init(&cdev, &fops);
    if (cdev_add(&cdev, dev_number, 1) < 0) {
        unregister_chrdev_region(dev_number, 1);
        return -1;
    }
    
    cls = class_create(THIS_MODULE, CLASS_NAME);
    if (IS_ERR(cls)) {
        cdev_del(&cdev);
        unregister_chrdev_region(dev_number, 1);
        return PTR_ERR(cls);
    }
    
    device_create(cls, NULL, dev_number, NULL, DEVICE_NAME);
    printk(KERN_INFO "var4 device driver loaded\n");
    return 0;
}

static void __exit dev_exit(void) {
    device_destroy(cls, dev_number);
    class_destroy(cls);
    cdev_del(&cdev);
    unregister_chrdev_region(dev_number, 1);
    printk(KERN_INFO "var4 device driver unloaded\n");
}

module_init(dev_init);
module_exit(dev_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("262512");
MODULE_DESCRIPTION("Character device driver for counting digits");
