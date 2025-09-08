# Task0的笔记在这里

## 0.1 hello world!

因为有一点点微薄的`Java`基础，所以哈喽沃德这块子还是比较简单的😎😎😎

~~所以受苦的部分还在后面~~

<img src=".\Pic\0.1.png" alt="helloworld!" style="zoom:50%;" />

## 0.2 hello world! ，但是Spring Boot项目

### 0.2.1实践前的废话

到了这里就是完全的有0个基础了😌😌😌

但是不慌！我先根据任务要求一步一步来，哪里不会学哪里✌

先把不了解的东西都标出来：

<img src=".\Pic\0.2.1.png" alt="0.2.1" style="zoom:80%;" />

- `Spring  Boot`项目是啥？怎么写？
- 接口是啥？怎么写？
- 如何让程序通过浏览器访问网页？
- 什么叫“给浏览器返回一个字符串”？

### 0.2.2 先搞Spring Boot

话说`Spring Boot`是什么❓

问了下DeepSeek，大概好像跟`C++`里的`Qt`是一个生态位的

不管了，我先去给好的`Spring`官方文档读一下

发现要用到什么`Maven`或者`Gradle`，这俩又是啥？

又问了下DeepSeek，大概好像跟`CMake`是一个生态位的

在问了下群中dalao后，决定先使用`Maven`

<img src=".\Pic\0.2.2.png" alt="0.2.2" style="zoom: 50%;" />

#### 0.2.2.1 用Maven设置项目

- 创建了第一个`pom.xml`

    文档给了我一个如下的代码，并让我在终端执行了指令`mvn package`

    ```xml
    <!--pom.xml-->
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>com.example</groupId>
        <artifactId>myproject</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>3.4.5</version>
        </parent>
    
        <!-- Additional lines to be added here... -->
    
    </project>
    ```

    完全不知道这是在干嘛😵‍💫😵‍💫😵‍💫

    问问DeepSeek，得知这个神必的`.xml`文件的作用大概是在提供一些基础信息，并且设定好一些基础配置

    而`mvn package`的作用是让`Maven`构建我的项目
    
- 添加依赖项

    依赖项是什么？大概就是我这个项目会用到的一些外部包

    同时文档让我添加了一下如下的依赖项

    ```xml
    <dependencies>
    	<dependency>
    		<groupId>org.springframework.boot</groupId>
    		<artifactId>spring-boot-starter-web</artifactId>
    	</dependency>
    </dependencies>
    ```

    望文生义的话，这个依赖项就是包含了一个基础的初始包

- 一些DIY

    既然能看得懂一些部分了，那么就先自己改点东西吧

    ```xml
    	<groupId>com.JoTang2025</groupId>
    	<artifactId>JavaDev</artifactId>
    	<version>0.0.1-SNAPSHOT</version>
    ```

    那么，到目前为止，就要开始写点代码（`Ctrl + C`, `Ctrl + V`）了

#### 0.2.2.2 编写代码

- 依旧复制/.

  值得一提的是，文档说`Maven`会默认从`src/main/java` 编译源代码

  那这里就把源码放在`src\main\java\com\JoTang2025\HelloWorldSpringBoot.java`好了

  给好的代码，稍微改个类名之后粘贴一下

  ```java
  package com.JoTang2025;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  @RestController
  @SpringBootApplication
  public class HelloWorldSpringBoot {
  
  	@RequestMapping("/hello")
  	String home() {
  		return "Hello World!";
  	}
  
  	public static void main(String[] args) {
  		SpringApplication.run(MyApplication.class, args);
  	}
  
  }
  ```

- 依旧看不懂😌😌😌
  
  看看文档，问问AI，自己写写注释吧
  
  ```java
  // 声明代码所属的Java包
  package com.JoTang2025;
  
  // 导入所需的各种外界包
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  // 形如@xxxxx的东西，叫注解
  // 注解就是一个能告诉Spring一些信息的标签
  
  @RestController	// 这个注解标记了这个类的返回值会以特定格式输出，并且类会处理网络请求
  @SpringBootApplication	// 这个注解用来启用SpringBoot框架的一些基础部分
  public class HelloWorldSpringBoot {
  
  	@RequestMapping("/hello")	//这个注解用来访问网址的哪个部分的时候调用home方法
  	String home() {
  		return "Hello World!";
  	}
  
  	public static void main(String[] args) {
          // 启动Spring Boot应用，调用HelloWorldSpringBoot这个组件
  		SpringApplication.run(HelloWorldSpringBoot.class, args);
  	}
  
  }
  ```
  

#### 0.2.2.3 尝试运行！

- 输入神奇代码`mvn spring-boot:run`

  等了一会儿之后就成功了！

  ![0.2.3](.\Pic\0.2.3.png)

- 根据教程在浏览器中打开`localhost:8080`...

<img src=".\Pic\0.2.4.png" alt="0.2.4" style="zoom:50%;" />

- 见证奇迹的时刻！

  ![0.2.5](.\Pic\0.2.5.png)

- 成功了！😭😭😭

## 0.3 理解

### 0.3.1 对Java的理解

Java是这方面的万物之基，一切东西的运行都搭载在我们伟大的JVM之上

瓦门🙏🙏

### 0.3.2 对JavaWeb的理解

JavaWeb是Java基础之上针对网络相关开发的特化方案

### 0.3.1 对Java和JavaWeb之间关联的理解

JavaWeb是基于Java而产生的针对网络相关开发的特化内容