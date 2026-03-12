package com.campus.market.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsVO {
    private Long id;
    private Long userId;
    private String title;
    private Integer categoryId;
    private BigDecimal originalPrice;
    private BigDecimal currentPrice;
    private String description;
    private String mainImage;
    private String location;
    private String condition;
    private String status;
    private Integer views;
    private LocalDateTime createTime;
    
    // 卖家信息
    private String userName;
    private String userAvatar;
    private Integer userCreditScore;
    
    // 其他扩展字段
    private Boolean isFavorite;
}
