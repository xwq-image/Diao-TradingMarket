package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("goods_id")
    private Long goodsId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("reply_to_id")
    private Long replyToId;
    
    private String content;
    
    @TableField("is_read")
    private Integer isRead;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
