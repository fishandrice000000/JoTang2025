# 从零开始的JavaWeb SpringBoot框架学习

## 注解

```java
@SpringBootApplication
/*
 * 标记主类
 * 也就是告诉SpringBoot，整个项目应该从哪里开始运行
 */

@ResponseBody
/*
 * 标记这个方法的返回值将作为响应体发送给客户端
 * 如果没有这个注解，那么SpringBoot会默认把返回值理解为资源目录中的一个HTML模板文件
 */

@Controller
/*
 * 标记这个类是一个Web控制器
 * 也就是说能够通过网络来访问
 */

@RestController
/* 
 * @ResponseBody + @Controller
 */

@RequestMapping()
@GetMapping()
@PostMapping()
/* 
 * 标记对应方法的访问路径，并且规定好允许的访问类型
 * 譬如@GetMapping(""/hello")，意味着想要调用对应的方法，只能通过GET请求来访问.../hello这个路径
 * 只有@RequestMapping()可以作用于类，它定义了这个类中所有方法的父路径
 */

/* --- 传入参数 --- */

@RequestParam(name = "PARAM", required = false/true)
/* 
 * 标记对应参数应在URL的查询参数或表单中寻找
 *
 * 同时也可以为这种类型传入的参数起别名
 * 例如@RequestParam(name = "age") Integer username
 */

@RequestBody
/*
 *标记对应参数应在请求的请求体中寻找
 */

/* --- 控制反转&依赖注入 --- */

@Component
@Controller
@Service
@Repository
/* 
 * 标记Bean对象，允许SpringBoot对这个类进行管理
 * 一般在定义一个类的时候使用
 */

@AutoWired
/* 
 * 告诉SpringBoot，拿取一个所标记的类来声明一个实例
 * 一般在使用一个类的时候使用
 */
```



