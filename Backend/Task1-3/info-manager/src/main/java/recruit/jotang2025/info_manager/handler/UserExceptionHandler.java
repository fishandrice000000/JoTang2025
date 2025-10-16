package recruit.jotang2025.info_manager.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import recruit.jotang2025.info_manager.controller.UserController;
import recruit.jotang2025.info_manager.exception.IllegalOperationException;
import recruit.jotang2025.info_manager.exception.UserNotFoundException;

@RestControllerAdvice(assignableTypes=UserController.class)
public class UserExceptionHandler {
    // 用户不存在异常
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 非法访问异常
    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<String> handleIllegalOperationException(IllegalOperationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // 非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
    // 权限不足异常
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
