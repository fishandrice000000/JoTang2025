package recruit.jotang2025.info_manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.service.LogInService;
import recruit.jotang2025.info_manager.utils.JwtUtils;

@RestController
public class LogInController {
    @Autowired
    private LogInService logInService;
    @Autowired
    private JwtUtils jwtUtils;

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
            String role = u.getRole().toString();
            claims.put("email", email);
            claims.put("mobile", mobile);
            claims.put("role", role);

            String jwt = jwtUtils.generate(claims, userId, 12L);

            return ResponseEntity.ok(jwt);
        }
    }
}
    