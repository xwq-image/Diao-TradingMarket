package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private String title;
    
    @TableField("category_id")
    private Integer categoryId;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    @TableField("current_price")
    private BigDecimal currentPrice;
    
    private String description;
    private String images;
    
    @TableField("main_image")
    private String mainImage;
    
    private String location;
    
    @TableField("`condition`")
    private String condition;
    
    private String status;
    private Integer views;
    
    @TableField("reject_reason")
    private String rejectReason;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
