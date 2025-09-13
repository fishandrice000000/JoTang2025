package recruit.jotang2025.info_manager.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public enum Privilege {
        admin, user
    }

    public enum Status {
        active, inactive, suspended
    }

    private Long userId;
    private Privilege privilege;
    private Status status;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
