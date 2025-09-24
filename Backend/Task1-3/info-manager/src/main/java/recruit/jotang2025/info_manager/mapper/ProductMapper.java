package recruit.jotang2025.info_manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import recruit.jotang2025.info_manager.pojo.Product;

@Mapper
public interface ProductMapper {
    //新增商品信息
    public Integer createProduct(Product product);

    //删除商品信息
    public Integer removeProduct(Long productId);

    //更新商品信息
    public Integer updateProduct(Product product);

    //按Id查询商品详情
    public Product queryProductById(Long productId);

    //查询商品列表
    public List<Product> queryAllProduct();
}
