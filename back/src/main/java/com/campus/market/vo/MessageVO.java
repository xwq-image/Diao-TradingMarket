package com.campus.market.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;
    private Long goodsId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long replyToId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
