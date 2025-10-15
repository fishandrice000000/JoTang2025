package recruit.jotang2025.info_manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.controller.ProductController;
import recruit.jotang2025.info_manager.controller.ProductOrderController;
import recruit.jotang2025.info_manager.exception.IllegalOperationException;
import recruit.jotang2025.info_manager.exception.OrderNotFoundException;
import recruit.jotang2025.info_manager.exception.ProductNotFoundException;
import recruit.jotang2025.info_manager.mapper.ProductMapper;
import recruit.jotang2025.info_manager.pojo.Product;
import recruit.jotang2025.info_manager.pojo.ProductOrder;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;

@SpringBootTest
@Transactional
public class ProductOrderApplicationTests {

        @Autowired
        ProductOrderController orderController;
        @Autowired
        ProductController productController;
        @Autowired
        ProductMapper productMapper;

        ProductOrder testOrder;
        Authentication adminAuth;
        Authentication userAuth;

        @BeforeEach
        public void init() {
                // 准备好测试用户
                adminAuth = AuthenticationUtils.generateAuthentication("1", "admin");
                userAuth = AuthenticationUtils.generateAuthentication("2", "user");

                // 默认用户权限为admin
                AuthenticationUtils.setAuthentication(adminAuth);

				// 准备好测试订单
                Long productId = 1L;
                Long buyerId = 1L;
                testOrder = new ProductOrder(
                                null,
                                productId,
                                buyerId,
                                ProductOrder.Status.ordered,
                                LocalDateTime.now(),
                                LocalDateTime.now());
        }

        @Test
        void testRemove() {
                orderController.createOrder(testOrder);
                Long toBeDeletedId = testOrder.getOrderId();

                orderController.removeOrder(toBeDeletedId);

                assertThrowsExactly(OrderNotFoundException.class,
                                () -> orderController.queryOrderById(toBeDeletedId),
                                "查询到已删除的订单时应报错");
        }

        @Test
        void testUpdate() {
                orderController.createOrder(testOrder);
                ProductOrder newOrder = new ProductOrder(
                                testOrder.getOrderId(), testOrder.getProductId(), testOrder.getBuyerId(),
                                testOrder.getStatus(),
                                testOrder.getCreateTime(), testOrder.getUpdateTime().plusDays(1));
                ProductOrder foundOrder;

                orderController.updateOrder(newOrder);
                foundOrder = orderController.queryOrderById(newOrder.getOrderId()).getBody();

                assertNotEquals(foundOrder.getUpdateTime(), testOrder.getUpdateTime(), "更新时间应已更新");

        }

        @Test
        void testQueryById() {
                orderController.createOrder(testOrder);
                ProductOrder foundOrder;

                foundOrder = orderController.queryOrderById(testOrder.getOrderId()).getBody();

                assertNotNull(foundOrder, "查询结果不应为空");
                // assertEquals(testOrder, foundOrder, "查询结果应相同");
                assertThrowsExactly(OrderNotFoundException.class,
                                () -> orderController.queryOrderById(666666L),
                                "查询不存在的订单时应报错");
        }

        @Test
        void testPlace() {
                // Arrange
                Product testProduct = new Product(
                                null,
                                "测试商品",
                                "大家好啊，我是测试商品，给大家一些好看的东西",
                                BigDecimal.TEN,
                                1L,
                                Product.Type.item,
                                Product.Status.unsold,
                                LocalDateTime.now(),
                                LocalDateTime.now());
                Product foundProduct;
                Product.Status beforeStatus;
                Product.Status afterStatus;

                ProductOrder foundOrder;
                ProductOrder productSoldOrder;
                ProductOrder productNotFoundOrder = new ProductOrder(
                                null,
                                666666L,
                                2L,
                                ProductOrder.Status.ordered,
                                LocalDateTime.now(),
                                LocalDateTime.now());

                Exception e;
                // Act
                beforeStatus = testProduct.getStatus();
                productController.createProduct(testProduct);

                testOrder.setBuyerId(2L);
                testOrder.setProductId(testProduct.getProductId());

                orderController.placeOrder(testOrder);
                foundProduct = productController.queryProductById(testOrder.getProductId()).getBody();
                afterStatus = foundProduct.getStatus();
                foundOrder = orderController.queryOrderById(testOrder.getOrderId()).getBody();

                productSoldOrder = orderController.queryOrderById(testOrder.getOrderId()).getBody();
                productSoldOrder.setProductId(3L);

                // Assert
                assertEquals(Product.Status.sold, afterStatus, "相关商品状态应为sold");
                assertNotEquals(afterStatus, beforeStatus, "相关商品状态应已修改");
                assertNotNull(foundOrder, "成功插入了订单");
                assertEquals(ProductOrder.Status.ordered, foundOrder.getStatus(), "订单初始状态应为Ordered");

                e = assertThrowsExactly(
                                ProductNotFoundException.class,
                                () -> orderController.placeOrder(productNotFoundOrder),
                                "订单无对应商品时应该报错");

                e = assertThrowsExactly(
                                OrderNotFoundException.class,
                                () -> orderController.placeOrder(null),
                                "传入空订单时报错");

                e = assertThrowsExactly(
                                IllegalOperationException.class,
                                () -> orderController.placeOrder(productSoldOrder),
                                "相关的商品已售出时应报错;");
        }

        @Test
        void testCancel() {
                // Arrange
                ProductOrder foundOrder;
                Product testProduct = new Product(
                                null,
                                "测试商品",
                                "大家好啊，我是测试商品，给大家一些好看的东西",
                                BigDecimal.TEN,
                                1L,
                                Product.Type.item,
                                Product.Status.unsold,
                                LocalDateTime.now(),
                                LocalDateTime.now());
                Product foundProduct;
                Product.Status beforeProductStatus;
                Product.Status afterProductStatus;
                ProductOrder.Status beforeOrderStatus;
                ProductOrder.Status afterOrderStatus;

                Exception e;
                // Act
                productController.createProduct(testProduct);

                testOrder.setBuyerId(2L);
                testOrder.setProductId(testProduct.getProductId());

                orderController.placeOrder(testOrder);
                foundProduct = productController.queryProductById(testOrder.getProductId()).getBody();

                beforeOrderStatus = testOrder.getStatus();
                beforeProductStatus = foundProduct.getStatus();

                orderController.cancelOrder(testOrder.getOrderId());

                foundProduct = productController.queryProductById(testOrder.getProductId()).getBody();
                foundOrder = orderController.queryOrderById(testOrder.getOrderId()).getBody();
                afterOrderStatus = foundOrder.getStatus();
                afterProductStatus = foundProduct.getStatus();

                // Assert
                assertEquals(Product.Status.unsold, afterProductStatus, "商品状态应为unsold");
                assertNotEquals(beforeProductStatus, afterProductStatus, "商品状态应更新");
                assertNotEquals(beforeOrderStatus, afterOrderStatus, "订单状态应更新");
                assertEquals(ProductOrder.Status.canceled, afterOrderStatus, "订单状态应为canceled");

                e = assertThrowsExactly(
                                IllegalOperationException.class,
                                () -> orderController.cancelOrder(testOrder.getOrderId()),
                                "重复取消订单时应报错");
        }
}
