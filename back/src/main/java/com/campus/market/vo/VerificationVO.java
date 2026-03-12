package com.campus.market.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VerificationVO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String realName;
    
    private String studentId;
    
    private String idCard;
    
    private String studentCardImage;
    
    private String idCardFront;
    
    private String idCardBack;
    
    private String status;
    
    private String rejectReason;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
