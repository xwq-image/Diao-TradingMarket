package com.campus.market.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageVO {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
    private Boolean isMine; // 是否是当前用户发送的
}
