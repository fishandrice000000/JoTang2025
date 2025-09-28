package recruit.jotang2025.info_manager.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("未找到订单ID:" + orderId);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
