#指定编译器
CC = gcc

#编译选项
#-Iinclude : 设置编译器头文件查找路径
CFLAGS = -g -O2 -Wall -Werror -Iinclude

#最终的可执行文件
TARGET = hello.out

#中间目标文件
OBJECTS = hello.o lib/add.o lib/mul.o

#设置源码查找位置
vpath %.c .:./lib

#设置头文件查找位置
vpath %.h .:./include

# TODO
$(TARGET) : $(OBJECTS)
	$(CC) $(CFLAGS) $^ -o $@

#设置头文件关联
hello.o : add.h hello.h
add.o : add.h hello.h
mul.o : mul.h hello.

#统一规则
%.o : %.c
	$(CC) $(CFLAGS) -c $< -o $@

#设置clean命令
.PHONY : clean
clean : 
	rm -rf $(TARGET) $(OBJECTS)