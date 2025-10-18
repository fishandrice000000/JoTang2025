package recruit.jotang2025.info_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.mapper.LogInMapper;
import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.utils.PasswordEncoderUtils;

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
