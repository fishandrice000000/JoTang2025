package recruit.jotang2025.info_manager.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import recruit.jotang2025.info_manager.pojo.User;

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
