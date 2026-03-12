package com.campus.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReportDTO {
    
    @NotNull(message = "被举报人ID不能为空")
    private Long reportedId;
    
    @NotBlank(message = "举报类型不能为空")
    private String type; // USER-用户 GOODS-商品
    
    private Long targetId; // 举报目标ID（商品ID或用户ID）
    
    @NotBlank(message = "举报原因不能为空")
    private String reason;
    
    private String description;
    
    private List<String> evidenceImages;
}
