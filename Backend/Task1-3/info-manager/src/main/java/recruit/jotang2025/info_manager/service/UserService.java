package recruit.jotang2025.info_manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.exception.UserNotFoundException;
import recruit.jotang2025.info_manager.mapper.UserMapper;
import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;
import recruit.jotang2025.info_manager.utils.PasswordEncoderUtils;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthenticationUtils authUtils;
    @Autowired
    private PasswordEncoderUtils encoder;

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

        // 加密 `
        User encodedUser = encoder.getEncodedUser(newUser);
        userMapper.register(encodedUser);

        User finalUser = queryUserByIdNoCheck(newUser.getUserId());

        return finalUser;
    }

    // 删除用户
    public User deleteUser(Long userId) {
        User toBeRemovedUser = queryUserByIdNoCheck(userId);

        if (toBeRemovedUser == null) {
            throw new UserNotFoundException("无法删除不存在的用户");
        }

        // 若当前用户为user
        if (authUtils.currentRoleIsUser()) {
            String toBeRemovedUserId = userId.toString();
            String currentUserId = authUtils.getCurrentUserId();
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
    // 必须传入完整的User对象
    public User updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("不能传入空的用户信息");
        }

        User foundUser = queryUserByIdNoCheck(user.getUserId());

        if (foundUser == null) {
            throw new UserNotFoundException("无法修改不存在的用户");
        }

        // 若当前用户为user
        if (authUtils.currentRoleIsUser()) {
            String currentUserId = authUtils.getCurrentUserId();
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
            // 加密
            updateUserNoCheck(encoder.getEncodedUser(foundUser));
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
            // 加密
            updateUserNoCheck(encoder.getEncodedUser(user));

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
