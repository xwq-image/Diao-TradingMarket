package com.campus.market.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageDTO {
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;
    
    @NotBlank(message = "留言内容不能为空")
    private String content;
    
    private Long replyToId;
}
