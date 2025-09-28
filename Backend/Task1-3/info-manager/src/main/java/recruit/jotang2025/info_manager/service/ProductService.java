package recruit.jotang2025.info_manager.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.exception.IllegalOperationException;
import recruit.jotang2025.info_manager.exception.ProductNotFoundException;
import recruit.jotang2025.info_manager.mapper.ProductMapper;
import recruit.jotang2025.info_manager.pojo.Product;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    // 新增商品信息
    public Integer createProduct(Product product) {
        // 传入空指针;
        if (product == null) {
            throw new IllegalArgumentException("不能传入空的商品信息");
        }
        return productMapper.createProduct(product);
    }

    // 删除商品信息
    public Integer removeProduct(Long productId) {
        // 对应的商品不存在
        if (queryProductById(productId) == null) {
            throw new IllegalOperationException("无法删除不存在的商品");
        }
        return productMapper.removeProduct(productId);
    }

    // 更新商品信息
    public Integer updateProduct(Product product) {
        // 目标商品不存在
        if (queryProductById(product.getProductId()) == null) {
            throw new ProductNotFoundException(product.getProductId());
        }
        return productMapper.updateProduct(product);
    }

    // 按Id查询商品详情
    public Product queryProductById(Long productId) {
        Product foundProduct = productMapper.queryProductById(productId);
        // 商品不存在
        if (foundProduct == null) {
            throw new ProductNotFoundException(productId);
        }
        return foundProduct;
    }

    // 查询商品列表
    public List<Product> queryAllProduct() {
        return productMapper.queryAllProduct();
    }

    // 根据筛选条件查询商品
    public List<Product> queryProductByFilters(
            String type,
            BigDecimal minPrice, BigDecimal maxPrice,
            LocalDateTime startTime) {
        // 最低价格高于最高价格
        if (minPrice != null && maxPrice != null && (minPrice.compareTo(maxPrice) == 1) ) {
            throw new IllegalArgumentException("最低价格不能高于最高价格");
        }
        List<Product> products = productMapper.queryProductByFilters(type, minPrice, maxPrice, startTime);
        return products;
    }
}
