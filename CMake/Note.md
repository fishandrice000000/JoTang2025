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

### 2.1 任务一：编译命令小试牛刀

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

#### 2.2.6 更多命令和一些常用的特殊变量

```makefile
# 当make无法在当前目录搜索到所需文件时, 会接着试图在通过该变量指定的目录<dir>中寻找
# 目录<dir>之间用冒号分隔, 如 foo:../bar
VPATH = <dir>
```

```makefile
# 为符合模式<pattern>的文件指定搜索目录<dir>
# <dir>为空, 则清除对应文件已指定的搜索目录
# <pattern>需要包含字符%来作为通配符, 如%.c代表着所有.c结尾的文件
vpath <pattern> <dir>
```

```makefile
#部分自动化变量

#当前规则的目标文件名
$@ 

#规则的第一个依赖项
$< 

#规则中的所有依赖项
$^
```

```makefile
# 通配符 %
# 下面这个规则的意义是: 所有的.o文件都是由.c文件编译来的
%.o : %.c
	gcc -c $@ -o $<
```

### 2.3 任务三：自己写CMakeLists.txt

#### 2.3.1 一个很简单的示例

```cmake
# 开头声明所需最低版本号
cmake_minimum_required(VERSION *.*)

# 设置工程名称
project(foo)

# 设置头文件搜索路径
include_directories(include)

# 设置变量
set(VAR foo.c bar.c)

# 查找匹配模式的文件
# <arg>可以是GLOB, 查找范围在当前目录; 也可以是GLOB_RECURSIVE, 查找范围在当前目录以及其子目录
# <var>是存储查找结果的变量名
file(<arg> <var> <pattern>)

#编译可执行文件
add_excutable(<name> <src>)
```

#### 2.3.2 一些碎碎念

我说这玩意儿真好用吧😀😀😀
不知道比手写Makefile高到哪里去了
感觉甚至应该直接学CMakeLists怎么写
初学Makefile现在都感觉有点浪费时间😭😭😭
那玩意儿我可是捣鼓了整整两天呐😭😭😭