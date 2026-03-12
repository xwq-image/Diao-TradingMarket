package com.campus.market.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class GoodsDTO {
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 50, message = "标题长度2-50字符")
    private String title;
    
    @NotNull(message = "分类不能为空")
    private Integer categoryId;
    
    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;
    
    @NotNull(message = "现价不能为空")
    @DecimalMin(value = "0.01", message = "现价必须大于0")
    private BigDecimal currentPrice;
    
    @NotBlank(message = "描述不能为空")
    @Size(max = 500, message = "描述最多500字")
    private String description;
    
    private String images;
    private String mainImage;
    
    @NotBlank(message = "交易地点不能为空")
    private String location;
    
    @NotBlank(message = "成色不能为空")
    private String condition;
}
