package recruit.jotang2025.info_manager.mapper;

import org.apache.ibatis.annotations.Mapper;

import recruit.jotang2025.info_manager.pojo.User;

@Mapper
public interface LogInMapper {
    public User logIn(String email, String mobile, String password);
}
