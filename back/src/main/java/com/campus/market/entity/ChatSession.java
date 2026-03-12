package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_session")
public class ChatSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user1_id")
    private Long user1Id;
    
    @TableField("user2_id")
    private Long user2Id;
    
    @TableField("goods_id")
    private Long goodsId;
    
    @TableField("last_message")
    private String lastMessage;
    
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;
    
    @TableField("user1_unread")
    private Integer user1Unread;
    
    @TableField("user2_unread")
    private Integer user2Unread;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
