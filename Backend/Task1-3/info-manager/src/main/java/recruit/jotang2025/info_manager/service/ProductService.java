package recruit.jotang2025.info_manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.mapper.ProductMapper;
import recruit.jotang2025.info_manager.pojo.Product;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    //新增商品信息
    public Integer createProduct(Product product) {
        // 如果传入空, 返回0;
        if (product == null) return 0;
        return productMapper.createProduct(product);
    }
    //删除商品信息
    public Integer removeProduct(Long productId) {
        // 如果ID对应的商品不存在, 返回0
        if(queryProductById(productId) == null) return 0;
        return productMapper.removeProduct(productId);
    }

    //更新商品信息
    public Integer updateProduct(Product product) {
        // 如果没有product对应的商品, 返回0
        if (queryProductById(product.getProductId()) == null) return 0;
        return productMapper.updateProduct(product);
    }

    //按Id查询商品详情
    public Product queryProductById(Long productId) {
        return productMapper.queryProductById(productId);
    }

    //查询商品列表
    public List<Product> queryAllProduct() {
        return productMapper.queryAllProduct();
    }
}
