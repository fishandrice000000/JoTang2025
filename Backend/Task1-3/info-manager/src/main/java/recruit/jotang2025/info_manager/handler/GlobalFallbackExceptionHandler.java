// package recruit.jotang2025.info_manager.handler;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// /**
//  * 兜底的异常处理器
//  * 因为影响SpringBoot抛出错误栈堆所以全都注释掉了
//  */
// @RestControllerAdvice
// public class GlobalFallbackExceptionHandler {

//     // 兜底运行时错误
//     @ExceptionHandler(RuntimeException.class)
//     public ResponseEntity<String> handleGeneralRuntimeException(RuntimeException e) {
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("运行时未知错误！");
//     }

//     // 兜底除此之外未知的其他所有错误
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<String> handleGeneralOtherException(Exception e) {
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("未知错误！");
//     }
// }
