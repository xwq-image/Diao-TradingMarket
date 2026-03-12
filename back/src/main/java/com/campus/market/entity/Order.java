package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("goods_id")
    private Long goodsId;
    
    @TableField("buyer_id")
    private Long buyerId;
    
    @TableField("seller_id")
    private Long sellerId;
    
    private BigDecimal price;
    private String status;
    
    @TableField("goods_snapshot")
    private String goodsSnapshot;
    
    @TableField("confirm_time")
    private LocalDateTime confirmTime;
    
    @TableField("payment_time")
    private LocalDateTime paymentTime;
    
    @TableField("ship_time")
    private LocalDateTime shipTime;
    
    @TableField("complete_time")
    private LocalDateTime completeTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
