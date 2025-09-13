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
  -- <col_attribute>属性，包括了数据类型、约束、位置、注释等等信息
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
    ADD CONSTRAINT <constraint> -- 增加表级约束条件
  -- 清空表（只清空内容，不改变结构）
  TRUNCATE TABLE <name>;
  DELETE FROM <name>;
  -- 删除表
  DROP COLUMN <name>;
  ```
  
- 什么是数据类型？

  >**数据类型（Data Type）**定义了数据在数据库中的存储格式、取值范围以及可执行的操作。
  >
  >​	——DeepSeek R1

  常用以下数据类型

  ```mysql
  -- 数值类
  TINYINT -- 一字节整数
  SMALLINT -- 二字节整数
  INT -- 四字节整数
  BIGINT -- 八字节整数
  DOUBLE -- 双精度浮点数
  DECIMAL(M, D) -- 精确存储小数，M为总位数，D为小数位数
  
  -- 字符类
  CHAR(N) -- 固定长度为N的字符串
  VARCHAR (N) -- 最大长度为N的字符串
  TEXT -- 长文本
  
  -- 时间类
  DATE -- 'YYYY-MM-DD'
  TIME -- 'HH:MM:SS'
  DATETIME -- 'YYYY-MM-DD HH:MM:SS'
  TIMESTAMP -- 时间戳
  
  -- 枚举与集合类
  ENUM('val1', 'val2', ...) -- 枚举类型，预定值中多选一
  SET('val1', 'val2', ...) -- 枚举类型，预定值中多选多
  ```

  

- 什么是约束条件？

  > **约束条件（Constraints）** 是强制存储在表中的数据必须满足的**规则**。
  > ​	——DeepSeek R1

  常用以下约束条件
  ```mysql
  -- 主键，确保表中所有行的此值都唯一，并且不为空
  PRIMARY KEY
  
  -- 外键，这个值必须存在于另一张表的主值或唯一值中
  FOREIGN KEY <another_table>(<col_name>)
  
  -- 唯一，确保表中所有行的此值都是唯一，无视空值
  UNIQUE
  
  -- 非空，确保此值不是空值
  NOT NULL
  
  -- 检查，确保此值满足某个表达式
  CHECK <expression>
  
  -- 默认，为此值自动添加默认值
  DEFAULT <expression>
  
  -- 自动更新，当其他列被修改时，该列自动修改为某个值
  ON UPDATE <expression> 	 		
  ```

- 此外还有一些常用属性

  ``` mysql
  -- 添加注释
  COMMENT 'message'
  
  -- 自增，通常用于生成唯一的id
  AUTO_INCREMENT
  
  -- 在某列前/后，用于标明列的位置
  FIRST <another_col_name>
  AFTER <another_col_name>
  ```

### 1.1.4 开始写😀😀😀

​	在学了一大堆东西：

- 首先是搞清楚了在哪里能方便地写`MySQL`

- 接着是搞清楚了要写个表会用到什么语句

​	之后，我终于能开始写自己的表了！！！

#### 1.1.4.1 打个草稿✍️✍️✍️

​	先稍微组织一下都要写哪些表、这些表都有哪些列

 - 用户`user`
   - 用户ID`user_id`
   - 权限`priviledge` : `admin/user`, 用于管理用户权限
   - 状态`status`: `active/inactive/suspended`, 激活/注销/封禁, 用于管理用户状态
   - 用户名`username`
   - 密码`password`: 使用Bcript加密(Bcript是什么之后再学)
   - 邮箱`email`: 唯一
   - 手机号`mobile`: 唯一
   - 注册时间`create_time`: 用户注册的时间
   - 更新时间`update_time`: 用户信息更新的时间
 - 商品`product`
   - 商品ID`product_id`
   - 名称`product_name`
   - 描述`product_description`
   - 价格`price`
   - 发布者ID`publisher_id`
   - 类型`type`: `item/service`
   - 状态`status`: `sold/unsold`
   - 发布时间`create_time`
   - 更新时间`update_time`
 - 商品订单`product_order`
   - 订单ID`order_id`
   - 商品ID`product_id`
   - 买家ID`buyer_id`
   - 状态`status`: `ordered/canceled`, 已下单/已取消
   - 创建时间`create_time`
   - 更新时间`update_time`

#### 1.1.4.2 正式上场😤😤😤

- 表`user`搞定！

  ![1.1.7](./Pic/1.1.7.png)

- 表`product`搞定！

  ![1.1.8](./Pic/1.1.8.png)

- 表`product_order`搞定！

  ![1.1.9](./Pic/1.1.9.png)

- 结果一览：

  <img src="./Pic/1.1.10.png" alt="1.1.10" style="zoom:50%;" />

## 1.2 Hello, MyBatis!

​	**完蛋！我被接口包围了！**

<img src="./Pic/1.2.1.png" alt="1.2.1" style="zoom: 80%;" />

​	依旧依旧看不懂/.

- 怎样实现`Java`程序同`MySQL`数据库的互动？

  答：使用`MyBatis`

### 1.2.1 