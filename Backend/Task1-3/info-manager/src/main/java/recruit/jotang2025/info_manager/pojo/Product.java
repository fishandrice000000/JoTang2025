package recruit.jotang2025.info_manager.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {

    public enum Type {
        item, service
    }

    public enum Status {
        sold, unsold
    }

    //进行一些必要的初始化
    private Long productId;
    private String productName;
    private String productDescription = "";
    private BigDecimal price;
    private Long publisherId;
    private Type type;
    private Status status = Product.Status.unsold;
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();
}
