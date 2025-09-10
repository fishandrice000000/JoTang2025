# Task1的笔记在这里

## 1.1 Hello, MySQL!

​	依旧看不懂/.

<img src="./Pic/1.1.1.png" alt="1.1.1" style="zoom:50%;" />

- 什么是数据库表？
- 怎么写数据库表？
- 不懂，反正要学`MySQL`了

### 1.1.1 先下载😡😡😡

​	先去官网找个Documentation看看吧

![1.1.2](./Pic/1.1.2.png)

​	一通捣鼓之后，成功了！

<img src="./Pic/1.1.3.png" alt="1.1.3" style="zoom:50%;" />

### 1.1.2 在哪写😵‍💫😵‍💫😵‍💫

- 先看看怎么能用`VS Code`来连接到我的``MySQL``数据库

  需要用到一个扩展:  `MySQL`

<img src="./Pic/1.1.4.png" style="zoom:50%;" />

- 然后编写好相应的数据

  主机名就是`MySQL`服务器所在主机的Host，因为我是通过远程连接`WSL`打开的`VS Code`，所以这里填本地的环回地址`123.0.0.1`就可以了

  端口没做修改的话就是默认的3306

  用户名和密码就正常填即可

  <img src="./Pic/1.1.5.png" alt="1.1.5" style="zoom:50%;" />

- 然后我们就连接成功了

  ![1.1.6](./Pic/1.1.6.png)

### 1.1.3 写什么❓❓❓

- 一些浅层的理解

  针对`MySQL`数据库的一切操作都是通过这个数据库的终端来执行的

  而我们编写的`.sql`文件实则就是一堆命令的集合，类似于`Windows`中的`.bat`文件

  所以接下来要学的，就是`MySQL`中都有哪些命令

#### 1.1.3.1 数据库操作

- 有什么基本操作？

  ```mysql
  -- 查询所有数据库
  SHOW DATABASES;
  -- 查询当前数据库
  SELECT DATABASE();
  -- 使用数据库
  USE <name>;
  -- 创建数据库
  CREATE DATABASE [IF NOT EXISTS] <name>;
  -- 删除数据库
  DROP DATABASE [IF EXISTS] <name>;
  ```

#### 1.1.3.2 表结构操作

- 有什么基本操作 ？

  ```mysql
  -- 创建表
  -- <col_attribute>包括了数据结构、约束、位置、注释等等信息
  CREATE TABLE [IF NOT EXISTS] <name> (
    <col_name> <col_attribute>, 
    <col_name> <col_attribute>,
      ...	
      <table_constraint> -- 表级约束条件
  ) [ENGINE = InnoDB] [DEFAULT CHARSET=utf8mb4]; -- InnoDB是一种数据库引擎，默认用这个就行，utf8mb4是一种字符集
  
  -- 重命名表
  RENAME TABLE <name> TO <new_name>
  
  -- 修改表
  ALTER TABLE <name>
    ADD [COLUMN] <col_name> <col_attribute>, -- 增加列
    DROP [COLUMN] <col_name>,	-- 删除列
    CHANGE [COLUMN] <col_name> <another_col_name> <col_attribute>, -- 修改列的名字和定义，必须重新指定其定义
    MODIFY [COLUMN] <col_name> <col_attribute>, -- 修改列的定义，必须重新指定其定义
    RENAME [COLUMN] <col_name> TO <new_name>; -- 修改列的名字
    
  -- 清空表（只清空内容，不改变结构）
  TRUNCATE TABLE <name>;
  DELETE FROM <name>;
  -- 删除表
  DROP COLUMN <name>;
  ```
  
- 什么是约束条件？

  > **约束条件（Constraints）** 是强制存储在表中的数据必须满足的**规则**。
  > ​	——DeepSeek
  >
  
  常用以下约束条件
  ```mysql
  -- 主键，确保表中所有行的此值都唯一，并且不为空
  PRIMARY KEY
  
  -- 外键，这个值必须存在于另一张表的主值或唯一值中
  FOREIGN KEY
  
  -- 唯一，确保表中所有行的此值都是唯一，无视空值
  UNIQUE
  
  -- 非空，确保此值不是空值
  NOT NULL
  
  -- 检查，确保此值满足某个表达式
  CHECK <expression>
  
  -- 默认，为此值自动添加默认值
  DEFAULT <expression>
  ```

- 此外还有一些常用属性

  ``` mysql
  -- 为列添加注释
  COMMENT 'message'
  
  -- 自增，通常用于生成唯一的id
  AUTO_INCREMENT
  
  -- 在某列前/后，用于标明列的位置
  FIRST <another_col_name>
  AFTER <another_col_name>
  ```

### 1.1.4 开始写😀😀😀

​	在学了一大堆东西

- 首先是搞清楚了在哪里能方便地写`MySQL`

- 接着是搞清楚了要写个表要会到什么语句

​	之后，我终于能开始写自己的表了！！！

​	多的也不用废话了，写就完事了