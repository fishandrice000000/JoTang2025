package recruit.jotang2025.info_manager.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import recruit.jotang2025.info_manager.exception.IllegalOperationException;
import recruit.jotang2025.info_manager.exception.ProductNotFoundException;
import recruit.jotang2025.info_manager.mapper.ProductMapper;
import recruit.jotang2025.info_manager.pojo.Product;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    // 新增商品信息
    public Integer createProduct(Product product) {
        // 传入空指针
        if (product == null) {
            throw new IllegalArgumentException("不能传入空的商品信息");
        }

        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentPublisherId = product.getPublisherId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            // 检查当前商品的publisherId是否为当前用户的userId
            if (!currentPublisherId.equals(currentUserId)) {
                throw new AccessDeniedException("无法创建属于别人的商品！");
            }
        }
        return productMapper.createProduct(product);
    }

    // 删除商品信息
    public Integer removeProduct(Long productId) {
        Product toBeRemovedProduct = queryProductById(productId);

        // 对应的商品不存在
        if (toBeRemovedProduct == null) {
            throw new IllegalOperationException("无法删除不存在的商品");
        }

        // 若当前用户为user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentPublisherId = toBeRemovedProduct.getPublisherId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            // 检查当前商品的publisherId是否为当前用户的userId
            if (!currentUserId.equals(currentPublisherId)) {
                throw new AccessDeniedException("无法删除属于别人的商品！");
            }
        }

        return productMapper.removeProduct(productId);
    }

    // 更新商品信息
    public Integer updateProduct(Product product) {
        // 传入参数为空
        if (product == null) {
            throw new IllegalArgumentException("传入参数不能为空！");
        }
        
        Product toBeUpdatedProduct = queryProductById(product.getProductId());

        // 目标商品不存在
        if (toBeUpdatedProduct == null) {
            throw new ProductNotFoundException(product.getProductId());
        }

        // 如果当前用户是user
        if (AuthenticationUtils.currentRoleIsUser()) {
            String currentPublisherId = toBeUpdatedProduct.getPublisherId().toString();
            String currentUserId = AuthenticationUtils.getCurrentUserId();
            if (!currentUserId.equals(currentPublisherId)) {
                throw new AccessDeniedException("无法修改属于别人的商品！");
            }
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
        if (minPrice != null && maxPrice != null && (minPrice.compareTo(maxPrice) == 1)) {
            throw new IllegalArgumentException("最低价格不能高于最高价格");
        }
        List<Product> products = productMapper.queryProductByFilters(type, minPrice, maxPrice, startTime);
        return products;
    }
}
