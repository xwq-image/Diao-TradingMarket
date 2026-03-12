package com.campus.market.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSessionVO {
    private Long id;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatar;
    private Long goodsId;
    private String goodsTitle;
    private String goodsImage;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}
