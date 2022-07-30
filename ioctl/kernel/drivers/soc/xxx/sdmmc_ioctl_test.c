#include <linux/init.h>
#include <linux/module.h>
#include <linux/miscdevice.h>
#include <linux/fs.h>
#include <linux/uaccess.h>
#include <linux/io.h>

#define CMD_TEST_0 _IO('A', 0)       //不需要读写的命令
#define CMD_TEST_1 _IOR('A', 1, int) //从内核读取一个int的命令
#define CMD_TEST_2 _IOW('A', 2, int) //向内核写入一个int的命令
#define CMD_TEST_3 _IOWR('A', 3, int) //读写一个int的命令

int misc_open(struct inode *a,struct file *b){
    printk("misc open \n");
    return 0;
}

int misc_release (struct inode * a, struct file * b){
    printk("misc file release\n");
    return 0;
}

long my_ioctl(struct file *fd, unsigned int cmd, unsigned long b){
    /*将命令按内容分解，打印出来*/
    printk("cmd type=(%c)\t nr=(%d)\t dir=(%d)\t size=(%d)\n", _IOC_TYPE(cmd), _IOC_NR(cmd), _IOC_DIR(cmd), _IOC_SIZE(cmd));
    
    /* 检查设备类型 */
    if (_IOC_TYPE(cmd) != IOC_MAGIC) {
        pr_err("[%s] command type [%c] error!\n", \
               __func__, _IOC_TYPE(cmd));
        return -ENOTTY; 
    }

    /* 检查序数 */
    if (_IOC_NR(cmd) > IOC_MAXNR) { 
        pr_err("[%s] command numer [%d] exceeded!\n", 
               __func__, _IOC_NR(cmd));
        return -ENOTTY;
    }
    
    /* 检查访问模式 */
    if (_IOC_DIR(cmd) & _IOC_READ)
        ret= !access_ok(VERIFY_WRITE, (void __user *)arg, \
                        _IOC_SIZE(cmd));
    else if (_IOC_DIR(cmd) & _IOC_WRITE)
        ret= !access_ok(VERIFY_READ, (void __user *)arg, \
                        _IOC_SIZE(cmd));
    if (ret)
        return -EFAULT;

    switch(cmd){
        case CMD_TEST_0://不需要读写的命令
            printk("CMD_TEST_0\n");
            break;
        case CMD_TEST_1://从内核读取一个int的命令
            printk("CMD_TEST_1\n");
            return 1;
            break;
        case CMD_TEST_2://向内核写入一个int的命令
            printk("CMD_TEST_2 date=(%d)\n",b);
            break;
        case CMD_TEST_3://读写一个int的命令
            printk("CMD_TEST_3 date=(%d)\n",b);
            return b+1;
            break;
    }

    return 0;
}

//文件操作集
struct file_operations misc_fops = {
    .owner = THIS_MODULE,
    .open = misc_open,
    .release = misc_release,
    .unlocked_ioctl = my_ioctl
    .compat_ioctl = my_ioctl
};

//ioctl_test，设备节点名,添加了这个语句可以在设备中ls找到这个设备节点，并且是c开头的
struct miscdevice misc_dev = {
    .minor = MISC_DYNAMIC_MINOR,
    .name = "ioctl_test",   
    .fops = &misc_fops
};

//这里的__exit,这是一个宏定义,同下面的__init类似
static __exit int ioctl_init(void){
    int ret;

    ret = misc_register(&misc_dev);  //注册杂项设备
    if(ret < 0){
        printk("misc regist failed\n");
        return -1;
    }

    printk("misc regist succeed\n");
    return 0;
}

//这里的__init，这是一个宏定义
//最常用的地方是驱动模块初始化函数的定义处，其目的是将驱动模块的初始化函数放入名叫.init.text的输入段
static __init void ioctl_exit(void){
    misc_deregister(&misc_dev);
}

//下面这两个宏，可以展开
MODULE_LICENSE("Dual BSD/GPL");
MODULE_AUTHOR("yangyang48");

//这里可以改成module_init
device_initcall_sync(ioctl_init); 
module_exit(ioctl_exit);  