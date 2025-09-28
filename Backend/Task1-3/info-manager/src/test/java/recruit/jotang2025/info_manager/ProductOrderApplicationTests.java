package recruit.jotang2025.info_manager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.controller.ProductOrderController;
import recruit.jotang2025.info_manager.exception.OrderNotFoundException;
import recruit.jotang2025.info_manager.pojo.ProductOrder;

@SpringBootTest
@Transactional
public class ProductOrderApplicationTests {

    @Autowired
    ProductOrderController orderCtrler;

    ProductOrder testOrder;

    @BeforeEach
    public void init() {
        Long productId = 1L;
        Long buyerId = 1L;
        testOrder = new ProductOrder(
                null, productId, buyerId,
                null,
                null, null);
        orderCtrler.createOrder(testOrder);
    }

    @Test
    void testRemove() {
        Long toBeDeletedId = testOrder.getOrderId();

        orderCtrler.removeOrder(toBeDeletedId);

        assertThrowsExactly(
                OrderNotFoundException.class,
                () -> orderCtrler.queryOrderById(toBeDeletedId),
                "查询到已删除的订单时应报错");
    }

    @Test
    void testUpdate() {
        LocalDateTime updateTime;
        ProductOrder newOrder = new ProductOrder(
                testOrder.getOrderId(), testOrder.getProductId(), testOrder.getBuyerId(),
                testOrder.getStatus(),
                testOrder.getCreateTime(), testOrder.getUpdateTime().plusDays(1));
        ProductOrder foundOrder;

        orderCtrler.updateOrder(newOrder);
        foundOrder = orderCtrler.queryOrderById(newOrder.getOrderId()).getBody();

        assertNotEquals(foundOrder.getUpdateTime(), testOrder.getUpdateTime(), "更新时间应已更新");
        
    }

    @Test
    void testQueryById() {
        ProductOrder foundOrder;

        
        foundOrder = orderCtrler.queryOrderById(testOrder.getOrderId()).getBody();

        assertNotNull(foundOrder, "查询结果不应为空");
        assertEquals(testOrder, foundOrder, "查询结果应相同");
        assertThrowsExactly(
                OrderNotFoundException.class,
                () -> orderCtrler.queryOrderById(666666L),
                "查询不存在的订单时应报错");
    }

    @Test
    void testPlace() {
        ;
    }

    @Test
    void testCancel() {
        ;
    }
}
