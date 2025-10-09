package recruit.jotang2025.info_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.mapper.LogInMapper;
import recruit.jotang2025.info_manager.pojo.User;

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
