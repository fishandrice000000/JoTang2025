package recruit.jotang2025.info_manager.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("未找到用户ID:" + userId);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
