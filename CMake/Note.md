# Makefile & CMakeLists笔记

## 1. 从 C 源码到可执行文件的完整流程

### 1.1 预处理

输入`.c`文件, 输出`.i`文件

- 在这一步:

  - 处理那些预处理指令, 如 `#define`, `#include`, `#ifdef`, `#ifndef`


  - 删除注释`// you cannot see me`


### 1.2 编译

输入`.i`文件, 输出汇编语言文件, 也就是介于源码和机器码之间的代码

如果平台不同, 生成的汇编语言也会不同

- 在这一步:
  - 检查词法和语法错误 `int mian ≠ int main`


### 1.3 汇编

输入汇编语言文件, 输出源码对应的机器码文件

- 在这一步:
  - 逐条翻译汇编语言为机器指令 `🤖:01001000 01100101 01101100 01101100 01101111 00100001`


### 1.4 链接

输入多个文件, 输出单一的可执行文件

- 在这一步:

  - 把一堆目标文件和库文件巴拉巴拉都组织在一起

  - 得到我们想要的可执行文件


### 1.5 总结

​       预处理           编译                 汇编                链接

`.c` -----------> `.i` -----------> `.s` -----------> `.o` -----------> `可执行文件`

---

## 2. 完成给定的Task

### 2.1 任务一 ：编译命令小试牛刀

```bash
# 将源码进行预处理, 并将处理后的文本输出到输出流中
gcc -E <file>

# 将文件翻译成汇编语言
gcc -S <file>

# 将文件翻译成机器码, 变成目标文件
gcc -c <file>

# 将文件编译成可执行文件
gcc <file>

# 参数-o, 可指定文件编译输出的文件名
gcc <file> -o <filename>
```

### 2.2 任务二：Makefile大展手脚

#### 2.2.1 Makefile的规则

**规则**就是Makefile执行命令的最小模块，一般有以下形式: 

```makefile
target : prerequisites
	recipe
```

- `target`:  期望的编译结果文件的文件名, 一般是`.o`文件

- `prerequisites`: 编译`target`所依赖的文件, 一般包括`target`对应的源码文件`.c`, 以及其所引用的头文件`.h`, 或者其它的`.o`文件

- `recipe`: 对应的`target`所会执行的指令, 一般包括`cc -c target.c`

#### 2.2.2 变量的使用

```makefile
# 声明变量
var = main.o foo.o bar.o

# 通过$()来引用变量
whatiwant : $(var)
	cc -o whatiwant $(var)
	
main.o : main.c apple.h
	cc -c main.c
	
foo.o : foo.c apple.h banana.h
	cc -c foo.c

bar.o : bar.c banana.h cherry.h
	cc -c bar.c
```

#### 2.2.3 自动推导

```makefile
var = main.o foo.o bar.o

# 通过$()来引用变量
whatiwant : $(var)
	cc -o whatiwant $(var)

# 文件***.c 和 命令cc -o ***.c 会被GNU自动推导出来
# 目标文件主导的写法
main.o : apple.h
foo.o : apple.h banana.h
bar.o : banana.h cherry.h

# 依赖文件主导的写法
main.o foo.o : apple.h
foo.o bar.o : banan.h
bar.o : cherry.h
```

#### 2.2.4 Makefile的原理

- 寻找第一个目标文件
- 再根据它的`prerequisites`来生成所需的目标文件
- 在生成这些所需的目标文件时, 继续生成它们的`prerequisites`所需要的目标文件
- 如此递归直到生成完毕
- 没有被用到的规则可以通过`make <target>`来单独调用

#### 2.2.5 自定义快捷命令

利用上文说到的最后一条性质, 自定义一个清除垃圾文件的命令`clean`

```makefile
var = main.o foo.o bar.o

whatiwant : $(var)
	cc -o whatiwant $(var)

main.o : apple.h
foo.o : apple.h banana.h
bar.o : banana.h cherry.h

# 使用.PHONY确保clean不会被真的识别为一个文件, 以免莫名其妙出现一堆clean.o
.PHONY : clean 
clean : 
# 在rm前加上-, 确保如果部分目标文件不存在, 其余目标文件也能被正常删除
	-rm whatiwant $(var)
```

