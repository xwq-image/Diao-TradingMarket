package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("verification")
public class Verification {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("student_id")
    private String studentId;
    
    @TableField("id_card")
    private String idCard;
    
    @TableField("student_card_image")
    private String studentCardImage;
    
    @TableField("id_card_front")
    private String idCardFront;
    
    @TableField("id_card_back")
    private String idCardBack;
    
    @TableField("status")
    private String status; // PENDING-待审核 APPROVED-已通过 REJECTED-已驳回
    
    @TableField("reject_reason")
    private String rejectReason;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
