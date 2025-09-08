#指定编译器
CC = gcc

#编译选项
CFLAGS = -g -O2 -Wall -Werror -Iinclude

TARGET = hello.out

OBJECTS = hello.o lib/add.o lib/mul.o
 

# TODO
$(TARGET) : $(OBJECTS)
	$(CC) $(CFLAGS) $^ -o $@

hello.o : hello.c ./include/hello.h ./include/add.h
	$(CC) $(CFLAGS) -c $< -o $@

add.o : ./lib/add.c ./include/hello.h ./include/add.h 
	$(CC) $(CFLAGS) -c $< -o $@

mul.o : ./lib/mul.c ./include/hello.h ./include/mul.h
	$(CC) $(CFLAGS) -c $< -o $@
	
.PHONY : clean

clean : 
	rm -rf $(TARGET) $(OBJECTS)