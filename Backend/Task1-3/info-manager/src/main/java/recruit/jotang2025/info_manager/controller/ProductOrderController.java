package recruit.jotang2025.info_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import recruit.jotang2025.info_manager.pojo.ProductOrder;
import recruit.jotang2025.info_manager.service.ProductOrderService;

@RestController
@RequestMapping("/order")
public class ProductOrderController {
    @Autowired
    private ProductOrderService productOrderService;

    // 创建订单
    @PostMapping("/create")
    public ResponseEntity<ProductOrder> createOrder(@RequestBody ProductOrder order) {
        productOrderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // 删除订单
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeOrder(@RequestParam("id") Long orderId) {
        productOrderService.removeOrder(orderId);
        return ResponseEntity.ok("订单" + orderId + "删除成功！");
    }

    // 更新订单
    @PostMapping("/update")
    public ResponseEntity<ProductOrder> updateOrder(@RequestBody ProductOrder order) {
        productOrderService.updateOrder(order);
        return ResponseEntity.ok(order);
    }

    // 按ID查询订单
    @GetMapping("/queryById")
    public ResponseEntity<ProductOrder> queryOrderById(@RequestParam("id") Long orderId) {
        ProductOrder founOrder = productOrderService.queryOrderById(orderId);
        return ResponseEntity.ok(founOrder);
    }

    // 下单
    @PostMapping("/placeOrder")
    public ResponseEntity<ProductOrder> placeOrder(@RequestBody ProductOrder order) {
        productOrderService.placeOrder(order);
        return ResponseEntity.ok(order);
    }

    // 取消订单
    @DeleteMapping("/cancelOrder")
    public ResponseEntity<String> cancelOrder(@RequestParam("id") Long orderId) {
        productOrderService.cancelOrder(orderId);
        return ResponseEntity.ok("订单" + orderId + "取消成功！");
    }
}
