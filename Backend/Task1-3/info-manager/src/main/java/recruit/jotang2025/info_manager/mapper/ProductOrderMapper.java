package recruit.jotang2025.info_manager.mapper;

import org.apache.ibatis.annotations.Mapper;

import recruit.jotang2025.info_manager.pojo.ProductOrder;

@Mapper
public interface ProductOrderMapper {
    // 创建订单
    public Integer createOrder(ProductOrder order);

    // 删除订单
    public Integer removeOrder(Long orderId);
    
    // 更新订单
    public Integer updateOrder(ProductOrder order);

    // 按ID查询订单
    public ProductOrder queryOrderById(Long orderId);

    // // 根据筛选条件查询订单
    // public List<ProductOrder> queryOrderByFilters(
    //         String type,
    //         BigDecimal minPrice, BigDecimal maxPrice,
    //         LocalDateTime startTime);
}
