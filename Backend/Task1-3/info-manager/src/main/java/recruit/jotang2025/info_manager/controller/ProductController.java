package recruit.jotang2025.info_manager.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import recruit.jotang2025.info_manager.pojo.Product;
import recruit.jotang2025.info_manager.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    // 新增商品信息
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // 删除商品信息
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<String> removeProduct(@RequestParam("id") Long productId) {
        productService.removeProduct(productId);
        return ResponseEntity.ok("商品" + productId + "删除成功！");
    }

    // 更新商品信息
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return ResponseEntity.ok(product);
    }

    // 按Id查询商品详情
    @GetMapping("/queryById")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<Product> queryProductById(@RequestParam("id") Long productId) {
        Product newProduct = productService.queryProductById(productId);
        return ResponseEntity.ok(newProduct);
    }

    // 查询商品列表
    @GetMapping("/queryAll")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public  ResponseEntity<List<Product>> queryAllProduct() {
        List<Product> newProducts = productService.queryAllProduct();
        return ResponseEntity.ok(newProducts);
    }

    // 根据筛选条件查询商品
    @GetMapping("/queryByFilters")
    @PreAuthorize("hasAnyRole('admin', 'user')")
    public ResponseEntity<List<Product>> queryProductByFilters(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "startTime", required = false) LocalDateTime startTime) {
        List<Product> products = productService.queryProductByFilters(type, minPrice, maxPrice, startTime);
        return ResponseEntity.ok(products);
    }
}
