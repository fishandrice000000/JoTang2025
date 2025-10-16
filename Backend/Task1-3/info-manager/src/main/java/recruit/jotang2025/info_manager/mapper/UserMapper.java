package recruit.jotang2025.info_manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import recruit.jotang2025.info_manager.pojo.User;

@Mapper
public interface UserMapper {
    // 注册用户
    public User register(User user);

    // 删除用户
    public Integer deleteUser(Long userId) ;

    // 更新用户
    public Integer updateUser(User user);

    // 查询用户
    public List<User> queryUser(Long userId);

    // 封禁用户
    public Integer banUser(Long userId);
}
