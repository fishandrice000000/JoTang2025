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

​	为了实现筛选只允许持有合法的`JWT`的访问，在询问`Gemini`后我可以使用`Java Security`的`Filter Chain`来解决这个问题。

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

