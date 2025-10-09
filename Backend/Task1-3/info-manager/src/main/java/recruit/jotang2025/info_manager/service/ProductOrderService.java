package recruit.jotang2025.info_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.exception.IllegalOperationException;
import recruit.jotang2025.info_manager.exception.OrderNotFoundException;
import recruit.jotang2025.info_manager.exception.ProductNotFoundException;
import recruit.jotang2025.info_manager.mapper.ProductMapper;
import recruit.jotang2025.info_manager.mapper.ProductOrderMapper;
import recruit.jotang2025.info_manager.pojo.Product;
import recruit.jotang2025.info_manager.pojo.ProductOrder;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;

@Service
public class ProductOrderService {
    @Autowired
    private ProductOrderMapper productOrderMapper;
    @Autowired
    private ProductMapper productMapper;

    // 创建订单
    public Integer createOrder(ProductOrder order) {
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            throw new AccessDeniedException("非管理员无法访问该接口！");
        }
        return productOrderMapper.createOrder(order);
    }

    // 删除订单
    public Integer removeOrder(Long orderId) {
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            throw new AccessDeniedException("非管理员无法访问该接口！");
        }
        return productOrderMapper.removeOrder(orderId);
    }

    // 更新订单
    public Integer updateOrder(ProductOrder order) {
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            throw new AccessDeniedException("非管理员无法访问该接口！");
        }
        return productOrderMapper.updateOrder(order);
    }

    // 按ID查询订单
    public ProductOrder queryOrderById(Long orderId) {
        ProductOrder foundOrder = productOrderMapper.queryOrderById(orderId);
        // 未查询到订单
        if (foundOrder == null) {
            throw new OrderNotFoundException(orderId);
        }

        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentBuyerId = foundOrder.getBuyerId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentBuyerId)) {
                throw new AccessDeniedException("无法查询属于别人的订单！");
            }
        }

        return foundOrder;
    }

    // 下单
    @Transactional // 确保在操作数据库时要么全成功要么全失败 不会出现订单表更新失败，但是商品表更新成功的情况
    public Integer placeOrder(ProductOrder order) {
        ProductOrder newOrder = order;
        // 订单不存在
        if (newOrder == null) {
            throw new OrderNotFoundException("place:目标订单不存在");
        }

        Product relatedProduct = productMapper.queryProductById(newOrder.getProductId());
        // 对应商品不存在
        if (relatedProduct == null) {
            throw new ProductNotFoundException("订单对应的商品不存在");
        }
        // 商品已售出
        if (relatedProduct.getStatus() != Product.Status.unsold) {
            throw new IllegalOperationException("商品已售出");
        }
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentBuyerId = order.getBuyerId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentBuyerId)) {
                throw new AccessDeniedException("无法替别人下单！");
            }
        }

        // 设置状态
        relatedProduct.setStatus(Product.Status.sold);
        newOrder.setStatus(ProductOrder.Status.ordered);
        // 创建订单&更新商品信息
        productMapper.updateProduct(relatedProduct);
        return createOrder(newOrder);
    }

    // 取消订单
    @Transactional
    public Integer cancelOrder(Long orderId) {
        ProductOrder toBeCanceledOrder = queryOrderById(orderId);

        // 订单已取消
        if (toBeCanceledOrder.getStatus() != ProductOrder.Status.ordered) {
            throw new IllegalOperationException("无法重复取消订单");
        }
        Product foundProduct = productMapper.queryProductById(toBeCanceledOrder.getProductId());
        // 商品不存在
        if (foundProduct == null) {
            throw new ProductNotFoundException("订单对应的商品不存在");
        }
        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentBuyerId = toBeCanceledOrder.getBuyerId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentBuyerId)) {
                throw new AccessDeniedException("无法取消别人的订单！");
            }
        }

        // 设置状态
        foundProduct.setStatus(Product.Status.unsold);
        toBeCanceledOrder.setStatus(ProductOrder.Status.canceled);
        // 更新状态
        productMapper.updateProduct(foundProduct);
        return updateOrder(toBeCanceledOrder);

    }

}
