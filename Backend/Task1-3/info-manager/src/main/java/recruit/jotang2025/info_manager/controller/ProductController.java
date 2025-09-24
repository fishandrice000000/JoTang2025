package recruit.jotang2025.info_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    //新增商品信息
    @PostMapping("/create")
    public Integer createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    //删除商品信息
    @GetMapping("/remove")
    public Integer removeProduct(@RequestParam("id") Long productId) {
        return productService.removeProduct(productId);
    }

    //更新商品信息
    @PostMapping("/update")
    public Integer updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    //按Id查询商品详情
    @GetMapping("/queryById")
    public Product queryProductById(@RequestParam("id") Long productId) {
        return productService.queryProductById(productId);
    }

    //查询商品列表
    @GetMapping("/queryAll")
    public List<Product> queryAllProduct() {
        return productService.queryAllProduct();
    }
}
