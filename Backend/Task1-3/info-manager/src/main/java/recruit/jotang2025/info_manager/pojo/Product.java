package recruit.jotang2025.info_manager.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    public enum Type {
        item, service
    }

    public enum Status {
        sold, unsold
    }

    private Long product_id;
    private String product_name;
    private String product_description;
    private BigDecimal price;
    private Long publisher_id;
    private Type type;
    private Status status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
