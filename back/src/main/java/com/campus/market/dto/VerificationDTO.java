package com.campus.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerificationDTO {
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    @NotBlank(message = "学号不能为空")
    private String studentId;
    
    private String idCard;
    
    private String studentCardImage;
    
    private String idCardFront;
    
    private String idCardBack;
}
