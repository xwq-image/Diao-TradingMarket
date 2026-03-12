package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private Long sessionId;
    
    @TableField("sender_id")
    private Long senderId;
    
    @TableField("receiver_id")
    private Long receiverId;
    
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
