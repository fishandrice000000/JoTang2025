package recruit.jotang2025.info_manager.pojo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductOrder {

    public enum Status {
        ordered, canceled, completed
    }

    private Long orderId;
    private Long productId;
    private Long buyerId;
    private Status status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
