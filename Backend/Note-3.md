# Task3的笔记在这里

## 3.1 我也喜欢RPG😋😋😋

![3.1.1](./Pic/3.1.1.png)

- 对于用户角色的划分，在`Task1`设计`User`的`POJO`时我们就已经做好了规划，为用户的权限设计了`admin`和`user`两种划分
- 所以接下来要做的只有设置接口的权限了，因为*出题人的仁慈*，我只需要关心`ProductOrder`模块的鉴权操作
- 但是经由我个人评估，鉴权的具体实现是个比较麻烦的事情，所以我先去实现**登录**功能

## 3.2 诺曼底*登录*🪖🪖🪖

###  3.2.1 登录接口的一些规划

- 登录使用接口`/login`, 请求体应包含`email/mobile`和`password`两个内容
- 登录也就是在数据库`user`中查询是否存在密码和`email/mobile`对得上的用户，如果查询结果为`null`即为登录失败
- 登录成功后，生成相应的`JWT`返回给浏览器，`JWT`中应包含`userId`,`username`,`email`,`mobile`

### 3.2.2 规划好了就开干💪💪💪

- 先封装一下`JWT`的生成和解析

```java
public class JWT {
    final private static String KEY_STRING = "recruit.jotang.2025.info_manager";
    final private static SecretKey KEY = Keys.hmacShaKeyFor(KEY_STRING.getBytes());

    // 生成JWT
    public static String generate(Map<String, ?> claims, String subject, Long lifeTime) {
        
        Date now = new Date();
        Long expirationMills = now.getTime() + TimeUnit.HOURS.toMillis(lifeTime);
        Date expiratioTime = new Date(expirationMills);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("2025JoTang")
                .issuedAt(now)
                .expiration(expiratioTime)

                .signWith(KEY)
                .compact();
    }

    // 解析JWT
    public static Claims parse(String jwt) {
        String keyString = "recruit.jotang.2025.info_manager";
        SecretKey key = Keys.hmacShaKeyFor(keyString.getBytes());
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt);
        return jws.getPayload();
    }
}

```

- 然后搓一下`Controller`, `Service`, `Mapper`

```java
  @RestController
  public class LogInController {
      @Autowired
      LogInService logInService;
  
      // 用户登录
      // 请求体中应包含password, 和mobile或email
      @PostMapping("/login")
      public ResponseEntity<String> logIn(@RequestBody User user) {
  
          // 尝试获取登录用户
          User u = logInService.logIn(user);
          
          // 用户不存在, 登录失败
          if (u == null) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("登录失败");
          }
          // 登录成功, 返回相应的JWT令牌
          else {
              // JWT中包含的信息: 登录的用户ID, 用户邮箱, 用户手机号
              Map<String, Object> claims = new HashMap<>();
              String userId = u.getUserId().toString();
              String email = u.getEmail();
              String mobile = u.getMobile();
              claims.put("userId", userId);
              claims.put("email", email);
              claims.put("mobile", mobile);
  
              String jwt = JWT.generate(claims, userId, 12L);
  
              return ResponseEntity.ok(jwt);
          }
      }
  }
```

```java
  @Service
  public class LogInService {
      @Autowired
      LogInMapper logInMapper;
  
      // 用户登录
      public User logIn(User user) {
          String email = user.getEmail();
          String mobile = user.getMobile();
          String password = user.getPassword();
  
          return logInMapper.logIn(email, mobile, password);
      }
  }
```

```xml
  	<select id="logIn">
          SELECT * FROM user 
          <where>
              (email = #{email} AND password = #{password})
              OR
              (mobile = #{mobile} AND password = #{password})
          </where>
      </select>
```

### 3.2.3 测试，启动！

- 依旧编写测试类

```java
  @SpringBootTest
  public class LogInApplicationTests {
      @Autowired
      LogInController logInController;
      
      @Test
      void testLogInSuccessfully() {
          User emailUser = new User(); // userId: 2
          User mobileUser = new User(); // userId: 1
          ResponseEntity<String> emailEntity;
          ResponseEntity<String> mobileEntity;
          String jwtEmail;
          String jwtMobile;
          Claims payloadEmail;
          Claims payloadMobile;
  
          emailUser.setEmail("1472608489@qq.com");
          emailUser.setPassword("password");
          mobileUser.setMobile("19170833183");
          mobileUser.setPassword("password");
  
          emailEntity = logInController.logIn(emailUser);
          mobileEntity = logInController.logIn(mobileUser);
  
          jwtEmail = emailEntity.getBody();
          jwtMobile = mobileEntity.getBody();
  
          payloadEmail = JWT.parse(jwtEmail);
          payloadMobile = JWT.parse(jwtMobile);
  
          assertEquals("2", payloadEmail.get("userId"));
          assertEquals("1", payloadMobile.get("userId"));
      }
  
      @Test
      void testLogInUnsuccessfully() {
          User badPasswordUser = new User();
          User badEmailUser = new User();
          ResponseEntity<String> badPasswordEntity;
          ResponseEntity<String> badEmailEntity;
  
          badPasswordUser.setMobile("19170833183");
          badPasswordUser.setPassword("badpassword");
          badEmailUser.setEmail("bademail@bad.com");
          badEmailUser.setPassword("password");
  
          badPasswordEntity = logInController.logIn(badPasswordUser);
          badEmailEntity = logInController.logIn(badEmailUser);
  
          assertEquals(HttpStatusCode.valueOf(404), badPasswordEntity.getStatusCode());
          assertEquals(HttpStatusCode.valueOf(404), badEmailEntity.getStatusCode());
      }
  }
```

- 让我们运行`mvn test`

  ![3.2.1](./Pic/3.2.1.png)

​	成功！

## 3.3 我怎么知道你登录没有？😕😕😕

​	为了实现筛选只允许持有合法的`JWT`的访问，在询问`Gemini`后我可以使用`Java Security`的`Filter Chain`来解决这个问题

​	顾名思义，`Java Security`在这里就起到了一个保安的作用，先将请求都阻拦下来，并根据`SecurityContextHolder`中的存储的`Authentication`来获得此次请求的信息，然后将符合要求的放行

### 3.3.1 我是一名保安，爱吃小熊饼干🫡🫡🫡

​	前端接收到了`JWT`后，再次发送请求时，`JWT`会被存储在`Header`的`Authorization`的部分，通常以`Authorization: Bearer <token>`的形式

- 所以我写的`Filter`对于登录验证的处理逻辑大致如下：
  - 先从`Header` 中获取`Authorization`中的`JWT`
  
  - 如果获取失败，或获取后解析失败，则说明请求是未登录而发来的请求，那么不记录`Authentication`，直接放行
  
    随后这次请求会在之后的`Filter`中被拦截，并返回`401 Unauthorized`
  
  - 如果获取并解析成功，则将相关信息记录到`Authentication`，并存储到`SecurtiryContextHolder`中，之后再放行
  
- 那我们就开干吧

  ~~但是细节真不少~~

```java
  @Component
  // 继承OncePerRequestFilter来确保每一次外部访问时, 这个Filter都只会被调用一次, 来减少资源的浪费
  public class JwtAuthenticationFilter extends OncePerRequestFilter { 
      @Override
      protected void doFilterInternal(
              @NonNull HttpServletRequest request,    // 已经发来的HTTP请求
              @NonNull HttpServletResponse response,  // 即将发出的HTTP响应
              @NonNull FilterChain filterChain    // 过滤器链对象, 当前工作结束时应当调用此对象的dofilter方法来放行请求
          )  throws ServletException, IOException // 提前声明这个方法可能会抛出的异常类型
      {
          // 获取请求头中的authorization
          String authHead = request.getHeader("authorization");
          
          // 若没有获得到对应的token或者token格式错误, 直接放行
          // 因为没有Authentication对象被存入SecurityContextHolder, 所以放行之后Spring Security会自动抛出AccessDeniedException
          // 因此不必担心直接放行会有什么坏结果
          if (authHead == null || !authHead.startsWith("Bearer ")) {
              filterChain.doFilter(request, response);
              return;
          }
  
          // 从Header中提取JWT
          String jwt = authHead.substring(7);
          
          try {
          // 获取载荷
          Claims claims = JWT.parse(jwt);
          String role = claims.get("role", String.class);
          String userId = claims.get("userId", String.class);
          
          // 这些内容被封装在了AuthenticationUtils.generateAuthenticauion方法中
          // 创建Authentication对象, 以便后续访问获取当前访问的用户的身份、权限信息
          List<GrantedAuthority> authority = new ArrayList<>();
          GrantedAuthority a = new SimpleGrantedAuthority("ROLE_" + role);
          authority.add(a);
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                  userId, // Principal: 主体, 当前用户的身份标识
                  null,   // Crdentials: 凭证, 当前用户的验证凭证, 一般是密码; 但是在使用JWT时, 通常是先通过凭证来获得JWT, 所以在这里凭证就没有必要了, 所以设置为null`
                  authority // Authorities: 权限, 当前用户的权限
              );
  
          // 将Authentication对象存入SecurityContextHolder, 这之后Spring Security就有办法得知当前访问的用户的信息和权限了
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          
          }
          catch (JwtException e) {
              // 如果解析JWT期间抛出了异常, 那么在这里捕获这个异常, 以免它上浮被更高层捕获为未知异常
              // 不必担心直接放行会有坏结果, 原因也是因为如果抛出异常那么就不会有Authentication对象
              System.out.print("JWT解析异常! ");
          }
  
          // 放行
          filterChain.doFilter(request, response);
      }
  }
```

### 3.3.2 认证失败，请刷卡💀💀💀

​	当写好了自制`Filter`后还有一个问题——`SpringSecurity`的`FilterChain`并不认识我的`JwtAuthenticationFilter`😵‍💫😵‍💫😵‍💫

​	所以我还需要再写一个配置类来配置好相关的信息

```java
@Configuration  // 配置类的统一注解 
@EnableWebSecurity  // 启用Spring Security
@EnableMethodSecurity // 启用@PreAuthorize等在方法层面鉴权的注解
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 主要内容, 子类SecurityFilterChain包含了细节的配置内容
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 关闭CSRF防护
            // CSRF是针对有状态的应用的攻击, 而这里使用的JWT是无状态的认证, 所以不需要开启CSRF
            // 将其关闭反而在一些时候会更加便利
            .csrf(csrf -> csrf.disable())   

            // 将Session管理设置成无状态 
            // 因为使用了JWT, 后端只需要获取请求头的中的JWT就可以得知用户的信息
            // 所以不需要再开启Session了
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置URL的访问权限
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login").permitAll()  // 访问"/login"的请求总是被允许
                    .anyRequest().authenticated()   // 除此之外的请求都应当拥有授权
            )

            // 将自制Filter正式加入到FilterChain中
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build(); // 使用build()将HttpSecurity对象的变量http打包成SecurityFilterChain对象
    }
}
```

### 3.3.3 生子当如孙仲谋——鉴权

​	现在就到了最后激动人心的鉴权时刻了！

![3.3.1](./Pic/3.3.1.png)

​	根据要求在`Contoller`的方法前上加入`@PreAuthorize(hasAnyRole('user', 'admin'))`

​	以`ProductOrder`为例...

```java
@RestController
@RequestMapping("/order")
public class ProductOrderController {
    @Autowired
    private ProductOrderService productOrderService;

    // 创建订单
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<ProductOrder> createOrder(@RequestBody ProductOrder order) {
        productOrderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // 删除订单
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<String> removeOrder(@RequestParam("id") Long orderId) {
        productOrderService.removeOrder(orderId);
        return ResponseEntity.ok("订单" + orderId + "删除成功！");
    }

    // 更新订单
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<ProductOrder> updateOrder(@RequestBody ProductOrder order) {
        productOrderService.updateOrder(order);
        return ResponseEntity.ok(order);
    }

    // 按ID查询订单
    @GetMapping("/queryById")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<ProductOrder> queryOrderById(@RequestParam("id") Long orderId) {
        ProductOrder foundOrder = productOrderService.queryOrderById(orderId);
        return ResponseEntity.ok(foundOrder);
    }

    // 下单
    @PostMapping("/placeOrder")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<ProductOrder> placeOrder(@RequestBody ProductOrder order) {
        productOrderService.placeOrder(order);
        return ResponseEntity.ok(order);
    }

    // 取消订单
    @DeleteMapping("/cancelOrder")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<String> cancelOrder(@RequestParam("id") Long orderId) {
        productOrderService.cancelOrder(orderId);
        return ResponseEntity.ok("订单" + orderId + "取消成功！");
    }
}
```

​	还有`Service`也要动！
```java
@Service
public class ProductOrderService {
    @Autowired
    private ProductOrderMapper productOrderMapper;
    @Autowired
    private ProductMapper productMapper;

    // 创建订单
    public Integer createOrder(ProductOrder order) {
        return productOrderMapper.createOrder(order);
    }

    // 删除订单
    public Integer removeOrder(Long orderId) {
        return productOrderMapper.removeOrder(orderId);
    }

    // 更新订单
    public Integer updateOrder(ProductOrder order) {
        return productOrderMapper.updateOrder(order);
    }

    // 按ID查询订单
    public ProductOrder queryOrderById(Long orderId) {
        ProductOrder foundOrder = productOrderMapper.queryOrderById(orderId);
        // 未查询到订单
        if (foundOrder == null) {
            throw new OrderNotFoundException(orderId);
        }

        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentBuyerId = foundOrder.getBuyerId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentBuyerId)) {
                throw new AccessDeniedException("无法查询属于别人的订单！");
            }
        }

        return foundOrder;
    }

    // 下单
    @Transactional // 确保在操作数据库时要么全成功要么全失败 不会出现订单表更新失败，但是商品表更新成功的情况
    public Integer placeOrder(ProductOrder order) {
        ProductOrder newOrder = order;
        // 订单不存在
        if (newOrder == null) {
            throw new OrderNotFoundException("place:目标订单不存在");
        }

        Product relatedProduct = productMapper.queryProductById(newOrder.getProductId());
        // 对应商品不存在
        if (relatedProduct == null) {
            throw new ProductNotFoundException("订单对应的商品不存在");
        }
        // 商品已售出
        if (relatedProduct.getStatus() != Product.Status.unsold) {
            throw new IllegalOperationException("商品已售出");
        }
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentBuyerId = order.getBuyerId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentBuyerId)) {
                throw new AccessDeniedException("无法替别人下单！");
            }
        }

        // 设置状态
        relatedProduct.setStatus(Product.Status.sold);
        newOrder.setStatus(ProductOrder.Status.ordered);
        // 创建订单&更新商品信息
        productMapper.updateProduct(relatedProduct);
        return createOrder(newOrder);
    }

    // 取消订单
    @Transactional
    public Integer cancelOrder(Long orderId) {
        ProductOrder toBeCanceledOrder = queryOrderById(orderId);

        // 订单已取消
        if (toBeCanceledOrder.getStatus() != ProductOrder.Status.ordered) {
            throw new IllegalOperationException("无法重复取消订单");
        }
        Product foundProduct = productMapper.queryProductById(toBeCanceledOrder.getProductId());
        // 商品不存在
        if (foundProduct == null) {
            throw new ProductNotFoundException("订单对应的商品不存在");
        }
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentBuyerId = toBeCanceledOrder.getBuyerId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentBuyerId)) {
                throw new AccessDeniedException("无法取消别人的订单！");
            }
        }

        // 设置状态
        foundProduct.setStatus(Product.Status.unsold);
        toBeCanceledOrder.setStatus(ProductOrder.Status.canceled);
        // 更新状态
        productMapper.updateProduct(foundProduct);
        return updateOrder(toBeCanceledOrder);

    }

}
```

## 3.4 `POSTMAN`测试🤖🤖🤖

### 3.4.1 出师未捷🫠🫠🫠

​	当我满心欢喜地`mvn spring-boot:run`之后，`SpringBoot`这次抛出了一个`WARN`

```bash
2025-10-10T13:26:47.948+08:00  WARN 4790 --- [info-manager] [           main] .s.s.UserDetailsServiceAutoConfiguration : 

Using generated security password: aa63235e-d641-4156-a391-8993f20aec27

This generated password is for development use only. Your security configuration must be updated before running your application in production.
```

​	询问了伟大的`Claude`后，它告诉我这是因为我的自制配置类被默认配置覆盖了，需要我去`application.properties`中配置一下，禁用默认的安全防护

​	就像这样

```properties
# 禁用默认安全配置
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
```

​	这之后配置就成功了！

### 3.4.2 测试登录

- 成功返回了一个`JWT`

```bash
curl --location 'http://localhost:8080/login' \
--header 'Content-Type: application/json' \
--data '{
    "mobile": "19170833183",
    "email": "",
    "password": "password"
}'
```

![3.4.1](./Pic/3.4.1.png)

### 3.4.3 测试鉴权

- 身为`admin`可以随意创建一个`product`

```bash
  curl --location 'http://localhost:8080/product/create' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiYWRtaW4iLCJtb2JpbGUiOiIxOTE3MDgzMzE4MyIsInN1YiI6IjEiLCJpc3MiOiIyMDI1Sm9UYW5nIiwiaWF0IjoxNzYwMDc1ODU1LCJleHAiOjE3NjAxMTkwNTV9.FVwNhck6oZADPM2176QAq80Jp9OlRokpJvT3TlopH14' \
  --header 'Content-Type: application/json' \
  --data '{
      "productName": "test",
      "productDescription": "test",
      "price": "114.11",
      "publisherId": "2",
      "type": "item"
  }'
```

![3.4.2](./Pic/3.4.2.png)

- 若篡改了`JWT`则返回`403 FORBIDDEN`

```bash
curl --location 'http://localhost:8080/product/create' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiYWRtaW4iLCJtb2JpbGUiOiIxOTE3MDgzMzE4MyIsInN1YiI6IjEiLCJpc3MiOiIyMDI1Sm9UYW5nIiwiaWF0IjoxNzYwMDc1ODU1LCJleHAiOjE3NjAxMTkwNTV9.FVwNhck6oZADPM2176QAq80Jp9OlRokpJvT3TlopH14114514' \
--header 'Content-Type: application/json' \
--data '{
    "productName": "test",
    "productDescription": "test",
    "price": "114.11",
    "publisherId": "2",
    "type": "item"
}'
```

![3.4.3](./Pic/3.4.3.png)

- 身为`user`不能为别人下单

```bash
curl --location 'http://localhost:8080/order/placeOrder' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoidXNlciIsIm1vYmlsZSI6IjEzODg5MzQzNTI2IiwiZW1haWwiOiIxNDcyNjA4NDg5QHFxLmNvbSIsInN1YiI6IjIiLCJpc3MiOiIyMDI1Sm9UYW5nIiwiaWF0IjoxNzYwMDc2NDI1LCJleHAiOjE3NjAxMTk2MjV9.WCbbouP1qtWWdMF5TLu2kTLoPDPpEhEapbRDuuOjqZA' \
--header 'Content-Type: application/json' \
--data '{
    "productId": "1",
    "buyerId": "3"
}'
```

![3.4.4](./Pic/3.4.4.png)

- 身为`user`不能替别人取消订单

```bash
curl --location --request DELETE 'http://localhost:8080/order/cancelOrder?id=243' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoidXNlciIsIm1vYmlsZSI6IjEzODg5MzQzNTI2IiwiZW1haWwiOiIxNDcyNjA4NDg5QHFxLmNvbSIsInN1YiI6IjIiLCJpc3MiOiIyMDI1Sm9UYW5nIiwiaWF0IjoxNzYwMDc2NDI1LCJleHAiOjE3NjAxMTk2MjV9.WCbbouP1qtWWdMF5TLu2kTLoPDPpEhEapbRDuuOjqZA' \
--data ''
```

![3.4.5](./Pic/3.4.5.png)

​	~~报错信息好像有点问题，但是好修~~

### 3.4.4 大功告成？🥂🥂🥂

​	~~这里省略去了一些基础测试，这个部分终于搞定了！~~

​	因为新增加的鉴权功能，这导致之前写的测试类***全都*** `ERROR`了

​	不过幸好这不是一个大问题，只需要在`init()`中再增加一些东西就可以了

![3.4.6](./Pic/3.4.6.png)

![3.4.7](./Pic/3.4.7.png)

### 3.4.5 大功告成！🍻🍻🍻

​	好了，在修改之后就保持全绿了！

![3.4.8](./Pic/3.4.8.png)

## 3.5 `InfoManager`招新中...

​	关于如何实现用户注册，这个感觉就是`User`的`CRUD`操作，不难

​	现在来顺手写一写...

### 3.5.1 用户`CURD`等操作的一些规划

- 注册，使用接口`/register`

  - 注册者可自定义`username`, `password`, `email`, `mobile`
  - `status`默认为`active`
  - `role`默认为`user`
- 删除，使用接口`/delete`

  - 被删除的用户的`status`会被设置成`inactive`
  - `user`用户只能删除自己的用户
  - `admin`用户没有限制
- 更新，使用接口`/update`

  - `user`用户只能更新自己的`username`, `password`, `email`, `mobile`
  - `admin`用户没有限制
- 查询，使用接口`/query`
  - 支持按`id`查询、查询所有
  - 仅`admin`可用
  
- 封禁，使用接口`/ban`
  - 指定`userId`, 将对应的用户`status`设置为`suspended`
    - 若已经是`suspended`，则设置为`active`（解封）

  - 仅`admin`可用


### 3.5.2 开干

​	`Controller`

```java
package recruit.jotang2025.info_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    // 注册用户
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User newUser = userService.register(user);
        return ResponseEntity.ok(newUser);
    }

    // 删除用户
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<String> deleteUser(@RequestParam("id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("用户" + userId + "已删除！");
    }

    // 更新用户
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    // 查询用户
    @GetMapping("/query")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<List<User>> queryUser(@RequestParam(name = "id", required = false) Long userId) {
        List<User> foundUsers = userService.queryUser(userId);
        return ResponseEntity.ok(foundUsers);
    }

    // 封禁用户
    @PostMapping("/ban")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<String> banUser(@RequestParam("id") Long userId) {
        Boolean isBanned = userService.banUser(userId);
        if (isBanned) {
            return ResponseEntity.ok("用户" + userId + "已封禁!");
        } else {
            return ResponseEntity.ok("用户" + userId + "已解封!");
        }

    }

}
```

​	`Service`

```java
package recruit.jotang2025.info_manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.exception.UserNotFoundException;
import recruit.jotang2025.info_manager.mapper.UserMapper;
import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    private User queryUserByIdNoCheck(Long userId) {
        List<User> users = userMapper.queryUser(userId);
        return users.stream().findAny().orElse(null);
    }

    private Integer updateUserNoCheck(User user) {
        return userMapper.updateUser(user);
    }

    // 注册用户
    public User register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("不能传入空的用户信息");
        }

        if ((user.getEmail() == null || user.getEmail().isEmpty())
                && (user.getMobile() == null || user.getMobile().isEmpty())) {
            throw new IllegalArgumentException("邮箱和手机号不能同时为空！");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setMobile(user.getMobile());

        userMapper.register(newUser);

        return queryUserByIdNoCheck(newUser.getUserId());
    }

    // 删除用户
    public User deleteUser(Long userId) {
        User toBeRemovedUser = queryUserByIdNoCheck(userId);

        if (toBeRemovedUser == null) {
            throw new UserNotFoundException("无法删除不存在的用户");
        }

        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String toBeRemovedUserId = userId.toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            // 检查当前商品的publisherId是否为当前用户的userId
            if (!currentUserId.equals(toBeRemovedUserId)) {
                throw new AccessDeniedException("无法删除属于别人的账号！");
            }
        }

        toBeRemovedUser.setStatus(User.Status.inactive);
        updateUserNoCheck(toBeRemovedUser);

        return toBeRemovedUser;
    }

    // 更新用户
    public User updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("不能传入空的用户信息");
        }

        User foundUser = queryUserByIdNoCheck(user.getUserId());

        if (foundUser == null) {
            throw new UserNotFoundException("无法修改不存在的用户");
        }

        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            String currentToBeUpdatedUserId = user.getUserId().toString();
            if (!currentUserId.equals(currentToBeUpdatedUserId)) {
                throw new AccessDeniedException("无法修改属于别人的用户信息！");
            }

            if (!foundUser.getUserId().equals(user.getUserId())
                    || !foundUser.getRole().equals(user.getRole())
                    || !foundUser.getStatus().equals(user.getStatus())
                    || !foundUser.getCreateTime().equals(user.getCreateTime())) {
                throw new AccessDeniedException("修改了不允许修改的信息！");
            }

            foundUser.setUsername(user.getUsername());
            foundUser.setPassword(user.getPassword());
            foundUser.setEmail(user.getEmail());
            foundUser.setMobile(user.getMobile());
            updateUserNoCheck(foundUser);
            return foundUser;
        } else {
            foundUser.setUserId(user.getUserId());
            foundUser.setRole(user.getRole());
            foundUser.setStatus(user.getStatus());
            foundUser.setUsername(user.getUsername());
            foundUser.setPassword(user.getPassword());
            foundUser.setEmail(user.getEmail());
            foundUser.setMobile(user.getMobile());
            foundUser.setCreateTime(user.getCreateTime());
            updateUserNoCheck(user);

            return foundUser;
        }
    }

    // 查询用户
    public List<User> queryUser(Long userId) {
        return userMapper.queryUser(userId);
    }

    // 封禁用户
    public Boolean banUser(Long userId) {
        User foundUser = queryUserByIdNoCheck(userId);

        if (foundUser == null) {
            throw new UserNotFoundException("所要封禁的用户不存在");
        }

        User.Status currenStatus = foundUser.getStatus();

        switch (currenStatus) {
            case active -> {
                foundUser.setStatus(User.Status.suspended);
                updateUserNoCheck(foundUser);
                return true;
            }
            case suspended -> {
                foundUser.setStatus(User.Status.active);
                updateUserNoCheck(foundUser);
                return false;
            }
            case inactive -> throw new IllegalArgumentException("所要封禁的用户已注销");
            default -> throw new RuntimeException("未知错误！");
        }
    }
}

```

​	`Mapper`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="recruit.jotang2025.info_manager.mapper.UserMapper">

    <!-- register -->
    <insert id="register" useGeneratedKeys="true" keyProperty="userId"> 
        INSERT INTO user
        (
            username, password, email, mobile
        )
        VALUES 
        (
            #{username}, #{password}, #{email}, #{mobile}
        )
    </insert>

    <!-- deleteUser -->
    <update id="deleteUser"> 
        UPDATE user
        SET status = #{status}
        WHERE user_id = #{userId}
    </update>

    <!-- updateUser -->
    <update id="updateUser"> 
        UPDATE user 
        SET role = #{role},
            status = #{status},
            username = #{username},
            password = #{password},
            email = #{email},
            mobile = #{mobile},
            create_time = #{createTime}
        WHERE user_id = #{userId}
    </update>


    <!-- queryUser -->
    <select id="queryUser" resultType="recruit.jotang2025.info_manager.pojo.User"> 
        SELECT * FROM user
        <where>
            <if test="userId != null">
                AND user_id = #{userId}
            </if>
        </where>
    </select>


    <!-- banUser -->
    <update id="banUser"> 
        UPDATE user 
        SET status = #{status}
        WHERE user_id = #{userId}
    </update>
</mapper> 
```

### 3.5.3 测试、测试、测试

​	`Test`

```java
package recruit.jotang2025.info_manager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.controller.UserController;
import recruit.jotang2025.info_manager.exception.UserNotFoundException;
import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;

@SpringBootTest
@Transactional
public class UserApplicationTests {
    @Autowired
    UserController userController;

    User testNewUser;
    Authentication adminAuth;
    Authentication userAuth;

    @BeforeEach
    public void init() {
        adminAuth = AuthenticationUtils.generateAuthentication("1", "admin");
        userAuth = AuthenticationUtils.generateAuthentication("2", "user");

        testNewUser = new User();
        testNewUser.setUsername("test");
        testNewUser.setPassword("password");
        testNewUser.setEmail("test@test.com");
        testNewUser.setMobile("1145141919810");
    }

    @Test
    void testRegisterNoException() {
        AuthenticationUtils.setAuthentication(adminAuth);

        User newUser = userController.register(testNewUser).getBody();
        User foundUser = userController.queryUser(newUser.getUserId()).getBody().get(0);

        assertNotNull(newUser);
        assertNotNull(newUser.getUserId());
        assertEquals(User.Status.active, foundUser.getStatus());
        assertEquals(User.Role.user, foundUser.getRole());
    }

    @Test
    void testRegisterWithExeption() {
        AuthenticationUtils.setAuthentication(adminAuth);

        testNewUser.setMobile(null);
        testNewUser.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> userController.register(testNewUser));
        assertThrows(IllegalArgumentException.class, () -> userController.register(null));
    }

    @Test
    void testDeleteNoException() {
        AuthenticationUtils.setAuthentication(userAuth);

        userController.deleteUser(2L);

        AuthenticationUtils.setAuthentication(adminAuth);
        User foundUser = userController.queryUser(2L).getBody().get(0);

        assertEquals(User.Status.inactive, foundUser.getStatus());
    }

    @Test
    void testDeleteWithException() {
        AuthenticationUtils.setAuthentication(userAuth);

        assertThrows(UserNotFoundException.class, () -> userController.deleteUser(101010101L));
        assertThrows(AccessDeniedException.class, () -> userController.deleteUser(3L));
    }

    @Test
    void testUpdateNoException() {
        AuthenticationUtils.setAuthentication(adminAuth);

        User foundUser = userController.queryUser(2L).getBody().get(0);
        String beforeMobile = foundUser.getMobile();
        foundUser.setMobile("1111111111");

        userController.updateUser(foundUser);

        foundUser = userController.queryUser(2L).getBody().get(0);

        assertNotEquals(beforeMobile, foundUser.getMobile());
    }

    @Test
    void testUpdateWithException() {
        AuthenticationUtils.setAuthentication(adminAuth);

        User foundUser = userController.queryUser(3L).getBody().get(0);
        User newFoundUser = userController.queryUser(2L).getBody().get(0);
        User userWithIllegalChange = new User(newFoundUser.getUserId(), User.Role.admin, newFoundUser.getStatus(),
                newFoundUser.getUsername(), newFoundUser.getPassword(), newFoundUser.getEmail(),
                newFoundUser.getMobile(), newFoundUser.getCreateTime(), newFoundUser.getUpdateTime());

        AuthenticationUtils.setAuthentication(userAuth);
        assertThrows(AccessDeniedException.class, () -> userController.updateUser(foundUser));
        assertThrows(AccessDeniedException.class, () -> userController.updateUser(userWithIllegalChange));
        assertThrows(IllegalArgumentException.class, () -> userController.updateUser(null));
    }

    @Test
    void testQuery() {
        List<User> userById = userController.queryUser(1L).getBody();
        List<User> users = userController.queryUser(null).getBody();

        assertEquals(1, userById.size());
        assertNotEquals(1, users.size());
    }
}

```

​	~~因为写的时候很累，所以就没有加很多注释，写得也不是很规整~~

​	所幸最后结果是很好的！
<img src="./Pic/3.5.1.png" alt="3.5.1" style="zoom:50%;" />



## 3.6 加密通话🔒🔒🔒

​	如何实现对密码实行加密存储？

​	在经过~~询问`Gemini`~~刻苦钻研后，我发现`Spirng Security`中已经提供了`PasswordEncoder`接口，使用它就可以实现对密码的加密了

​	但是，对于出题人建议的`BCrypt`，这种加密算法又是什么呢？

### 3.6.1 天呐，是`BCrypt`大人😭😭😭

​	什么是`BCrypt`？

```一串加密后的
			-------- 一串BCrypt加密后的密文 --------
$2a$12$cZEDb9f2xXb4nO2E0iA4z.1VQ/9F7P3z5z5f5Y1b1X5Y5e5c5a5B5
\__/ \_/ \______________________/\_____________________________/
 Alg Cost        Salt (22 chars)             Hash (31 chars)
```

- 首先，它*不是*一个加密算法~~（所以说我上面的说法就漏洞百出了）~~

  因为`BCrypt`只是一个*密码哈希函数*，**单向**加密，无法解密

  换言之，拥有`DB`中所有密码数据的我，虽然可以验证用户输入的密码是否正确，

  但是也仅止步于验证，我*并没有办法知道*用户的密码到底是什么

- `BCrypt`的特点有**慢**、**自定义成本因子**、**内置盐**这三点

  - 慢，是指验证`BCrypt`加密往往需要比较长的时间，计算机一秒往往只能进行几十次到几百次尝试

    如此一来，暴力破解的时间成本就被极大地提高了，以此提高了安全性

  - 自定义成本因子，是指你可以自由地调整加密哈希计算的复杂度

    这样的话，`BCrypt`就可以适应变化的计算机性能，使其能够维持“慢”的特性

  - 内置盐，是指**盐**和加密哈希字符串一起生成而又存储在一起

    而**盐**，是指在加密时生成的、和密码原文附加在一起的一串随机数据

    即使密码相同，因为加密数据中还有不同的盐，所以生成出的加密结果也不同

### 3.6.2 实操！

​	上完了概念课，那么就该实操了！

- 第一步，在配置类中注册一个`@Bean`

<img src="./Pic/3.6.1.png" alt="3.6.1" style="zoom: 50%;" />

​	但是，这不得不令我困惑：**为什么一定要在`@Configuration`覆盖的配置类中注册这个`Bean`呢？我在随便一个其它什么类中注册不可以吗？**



​	在向`Gemini`反复沟通求证之后，我明白了：

​	对于`@Configuration`覆盖的配置类中的`Bean`方法，它们的管理比较特殊，`Spring`会对它们使用`CGLIB`来创建每个方法的代理，并将这些方法的代理对象实例注册到容器中

​	并且在每次调用这些`Bean`方法前，`Spring`都会选择使用容器中已有的实例，如果没有就先创建一个实例，再使用容器中刚创建的那个实例

​	这一切就确保了，配置类中所有的`Bean`方法在被调用时，永远都只会从容器中抽取那个固定的对象

​	也就是说，这避免了配置类中的`Bean`方法互相调用时，出现“明明是同一个方法，在工作的实例却有多个"，这种看着古怪同时也确实可能导致古怪错误的情况

​	并且总的来讲，`Bean`方法和`@Configuration`就是天生一对，就算只是机械地写码，也要避免将`Bean`方法设置在除了配置类以外的地方

- 到了第二步，就是在`UserService`中应用加密了

  先封装一个`PasswordEncoderUtils`

```java
@Component
public class PasswordEncoderUtils {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getEncodedUser(User u) {
        User encodedUser = new User();
        encodedUser.setUserId(u.getUserId());
        encodedUser.setRole(u.getRole());
        encodedUser.setStatus(u.getStatus());
        encodedUser.setUsername(u.getUsername());
        encodedUser.setPassword(passwordEncoder.encode(u.getPassword()));  //密码加密
        encodedUser.setEmail(u.getEmail());
        encodedUser.setMobile(u.getMobile());
        encodedUser.setCreateTime(u.getCreateTime());
        encodedUser.setUpdateTime(u.getUpdateTime());

        return encodedUser;
    }
}
```

​	然后再在`UserService`中应用它

<img src="./Pic/3.6.2.png" alt="3.6.2" style="zoom:50%;" />

<img src="./Pic/3.6.3.png" alt="3.6.3" style="zoom:50%;" />

### 3.6.3 测试！

​	第三步，就是测试了！

​	使用`PostMan`测试一下密码会不会正确加密

```bash
curl --location 'http://localhost:8080/user/register' \
--header 'Content-Type: application/json' \
--data '{
    "username": "test",
    "password": "123456",
    "mobile": "111111111"
}
```

<img src="./Pic/3.6.4.png" alt="3.6.4" style="zoom:50%;" />

​	成功！

​	接下来看看能否正常登录

```bash
curl --location 'http://localhost:8080/login' \
--header 'Content-Type: application/json' \
--data '{
    "mobile": "111111111",
    "email": "",
    "password": "123456"
}'
```

​	<img src="./Pic/3.6.5.png" alt="3.6.5" style="zoom:50%;" />

​	失败了！🫢🫢🫢

​	应该是在`LogInService`中没有加入验证密码的逻辑

​	先在`PasswordEncoderUtils`中加入一个方法：

```java
    public Boolean isMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
```

​	然后修改一下原来的代码...

```java
@Service
public class LogInService {
    @Autowired
    private LogInMapper logInMapper;
    @Autowired
    private PasswordEncoderUtils encoder;

    // 用户登录
    public User logIn(User user) {
        String email = user.getEmail();
        String mobile = user.getMobile();
        String password = user.getPassword();
        User foundUser = logInMapper.logIn(email, mobile);

        if(encoder.isMatch(password, foundUser.getPassword())) {
            return foundUser;
        } else {
            return null;
        }
    }
}
```

<img src="./Pic/3.6.6.png" alt="3.6.6" style="zoom:50%;" />

现在就万事大吉了~~~
