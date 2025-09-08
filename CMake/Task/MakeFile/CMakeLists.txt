#声明所需最低版本号
cmake_minimum_required(VERSION 3.10)

#设置工程名称
project(hello)

#设置头文件搜索路径
include_directories(include)

#将所有源文件放入变量SRC
file(GLOB SRC "*.c" "lib/*.c")

#编译可执行文件
add_executable(${PROJECT_NAME} ${SRC})