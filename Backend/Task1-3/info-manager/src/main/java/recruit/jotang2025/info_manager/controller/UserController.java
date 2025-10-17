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
    private UserService userService;

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
