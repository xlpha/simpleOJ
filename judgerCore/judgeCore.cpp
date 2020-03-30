#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/resource.h>
#include <sys/wait.h>

enum Status{
    Accept,
    CompileError,
    RuntimeError,
    TimeLimitExceed,
    MemoryLimitExceed,
    WrongAnswer,
};

struct result {
    int status;
    int timeUsed;
    int memoryUsed;
};

int timeLimit;      //时间限制 毫秒
int memoryLimit;    //内存限制 KB

/**
 * 设置进程资源限制
 */
void setProcessLimit() {
    struct rlimit rl;
    rl.rlim_cur = timeLimit / 1000;         //换算成秒
    rl.rlim_max = rl.rlim_cur + 1;
    setrlimit(RLIMIT_CPU, &rl);

    rl.rlim_cur = memoryLimit * 1024;       //换算成字节
    rl.rlim_max = rl.rlim_cur + 1024;
    setrlimit(RLIMIT_DATA, &rl);
}


void split(char** arr, char* str, const char* del) {
    char* s = NULL;
    s = strtok(str, del);
    while (s != NULL) {
        *arr++ = s;
        s = strtok(NULL, del);
    }
    *arr++ = NULL;
}

/*
 run the user process
*/
/**
 * 运行用户提交的程序
 */
void runCmd(const char* cmdStr, const char* in, const char* out) {
    char _cmdStr[100];
    strcpy(_cmdStr, cmdStr);
    char* cmd[20];
    split(cmd, _cmdStr, " ");
    int newstdin = open(in, O_RDONLY | O_CREAT, 0644);
    int newstdout = open(out, O_WRONLY | O_CREAT, 0644);
    setProcessLimit();
    if (newstdout == -1 || newstdin == -1) {
        perror("Failed to open file");
        exit(1);
    }
    dup2(newstdout, fileno(stdout));
    dup2(newstdin, fileno(stdin));
    if (execvp(cmd[0], cmd) == -1) {
        perror("Failed to start the process");
    }
    close(newstdin);
    close(newstdout);
}


/**
 * 监控运行用户提交程序的线程
 */
void monitor(pid_t pid, struct result* rest) {
    int status;
    struct rusage ru;
    if (wait4(pid, &status, 0, &ru) == -1) {
        perror("wait4 failure");
    }
    rest->timeUsed = ru.ru_utime.tv_sec * 1000
                     + ru.ru_utime.tv_usec / 1000
                     + ru.ru_stime.tv_sec * 1000
                     + ru.ru_stime.tv_usec / 1000;
    rest->memoryUsed = ru.ru_maxrss;
    if (WIFSIGNALED(status)) {
        switch (WTERMSIG(status)) {
            case SIGSEGV:
                if (rest->memoryUsed > memoryLimit)
                    rest->status = MemoryLimitExceed;
                else
                    rest->status = RuntimeError;
                break;
            case SIGALRM:
            case SIGXCPU:
                rest->status = TimeLimitExceed;
                break;
            default:
                rest->status = RuntimeError;
                break;
        }
    } else {
        if (rest->timeUsed > timeLimit)
            rest->status = TimeLimitExceed;
        else if (rest->memoryUsed > memoryLimit)
            rest->status = MemoryLimitExceed;
        else
            rest->status = Accept;
    }
}

int run(const char* cmdStr, const char* in, const char* out) {
    pid_t pid = vfork();
    if (pid < 0) {
        perror("fork error");
        exit(1);
    } else if (pid == 0) {
        runCmd(cmdStr, in, out);
    } else {
        struct result rest;
        monitor(pid, &rest);
        printf("{\"status\":\"%d\",\"timeUsed:\":\"%d\",\"memoryUsed:\":\"%d\"}\n", rest.status, rest.timeUsed,
               rest.memoryUsed);
    }
}

int main(int argc, char* argv[]) {
    const char* runCmd = argv[1];
    timeLimit = atoi(argv[2]);      //单位 毫秒
    memoryLimit = atoi(argv[3]);    //单位 KB
    const char* inFile = argv[4];
    const char* outFile = argv[5];
    run(runCmd, inFile, outFile);
    return 0;
}
