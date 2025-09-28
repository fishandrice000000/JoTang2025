package recruit.jotang2025.info_manager.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("未找到商品ID:" + productId);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
