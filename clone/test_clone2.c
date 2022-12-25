#define _GNU_SOURCE
#include <errno.h>
#include <fcntl.h>
#include <inttypes.h>
#include <linux/futex.h>
#include <pthread.h>
#include <sched.h>
#include <signal.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/prctl.h>
#include <sys/socket.h>
#include <sys/syscall.h>
#include <sys/uio.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <unistd.h>


#define errExit(msg)    do { perror(msg); exit(EXIT_FAILURE); \
                               } while (0)
#define PAGE_SIZE (4 * 1024)
char* pseudothread_stack = NULL;
typedef struct debugger_thread_info {
  pid_t crashing_tid;
  pid_t pseudothread_tid;
  siginfo_t* siginfo;
  void* ucontext;
}debugger_thread_info;
//这个空间至少是4k的整数倍
#define STACK_SIZE (1024 * 1024)    /* Stack size for cloned child */



static inline void futex_wait(volatile void* ftx, int value) {
  syscall(__NR_futex, ftx, FUTEX_WAIT, value, NULL, NULL, 0);
}

static pid_t gettid() {
  return syscall(__NR_gettid);
}

static pid_t __fork() {
  return clone(NULL, NULL, 0, NULL);
}

static int debuggerd_dispatch_pseudothread(void* arg)
{
	prctl(PR_SET_NAME, "pseudothread");
	
    debugger_thread_info* thread_info = (debugger_thread_info*)(arg);
	printf("->>>debuggerd_dispatch_pseudothread,pseudothread_tid(%d)\n", thread_info->pseudothread_tid);
    sleep(5);
	pid_t crash_dump_pid = __fork();
  if (crash_dump_pid == -1) {
	errExit("failed to crash_dump_pid fork");
  } else if (crash_dump_pid == 0) {
	//子进程
	printf("->>>debuggerd_dispatch_pseudothread child\n");
	sleep(20);
  }else
  {
  	printf("->>>debuggerd_dispatch_pseudothread father\n");
	sleep(20);
  }
}

static void debuggerd_signal_handler(int signal_number, siginfo_t* info, void* context) {
	printf("->>>debuggerd_signal_handler signal_number(%d)\n", signal_number);
  debugger_thread_info thread_info;
    thread_info.crashing_tid = gettid();
    thread_info.pseudothread_tid = -1;
    thread_info.siginfo = info;
    thread_info.ucontext = context;

  pid_t child_pid =
    clone(debuggerd_dispatch_pseudothread, pseudothread_stack,
          CLONE_THREAD | CLONE_SIGHAND | CLONE_VM | CLONE_CHILD_SETTID | CLONE_CHILD_CLEARTID,
          &thread_info, NULL, NULL, &thread_info.pseudothread_tid);
  if (child_pid == -1) {
    errExit("failed to spawn debuggerd dispatch thread");
  }
	printf("->>>before futex_wait debuggerd_signal_handler pseudothread_tid(%d)\n", thread_info.pseudothread_tid);
  // Wait for the child to start...
  futex_wait(&thread_info.pseudothread_tid, -1);

  // and then wait for it to terminate.
  futex_wait(&thread_info.pseudothread_tid, child_pid);
  printf("->>>after futex_wait debuggerd_signal_handler pseudothread_tid(%d)\n", thread_info.pseudothread_tid);
}

void debuggerd_init() {
	printf("->>>debuggerd_init\n");
  size_t thread_stack_pages = 8;
  void* thread_stack_allocation = mmap(NULL, PAGE_SIZE * (thread_stack_pages + 2), PROT_NONE,
                                       MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);
  if (thread_stack_allocation == MAP_FAILED) {
    errExit("failed to allocate debuggerd thread stack");
  }

  char* stack = (char*)(thread_stack_allocation) + PAGE_SIZE;
  if (mprotect(stack, PAGE_SIZE * thread_stack_pages, PROT_READ | PROT_WRITE) != 0) {
    errExit("failed to mprotect debuggerd thread stack");
  }

  // Stack grows negatively, set it to the last byte in the page...
  stack = (stack + thread_stack_pages * PAGE_SIZE - 1);
  // and align it.
  stack -= 15;
  pseudothread_stack = stack;

  struct sigaction action;
  memset(&action, 0, sizeof(action));
  sigfillset(&action.sa_mask);
  action.sa_sigaction = debuggerd_signal_handler;
  action.sa_flags = SA_SIGINFO;

  // Use the alternate signal stack if available so we can catch stack overflows.
  action.sa_flags |= SA_ONSTACK;
  sigaction(SIGSEGV, &action, NULL);
}

int main()
{
	printf("->>>init main\n");
	prctl(PR_SET_NAME, "main clone");
	debuggerd_init();
	sleep(2);
	char* a = NULL;
	a[0] = 'a';
    exit(EXIT_SUCCESS);
}
