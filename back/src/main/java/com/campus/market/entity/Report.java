package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report")
public class Report {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("reporter_id")
    private Long reporterId;
    
    @TableField("reported_id")
    private Long reportedId;
    
    private String type; // USER-用户 GOODS-商品
    
    @TableField("target_id")
    private Long targetId;
    
    private String reason;
    
    private String description;
    
    @TableField("evidence_images")
    private String evidenceImages; // JSON数组
    
    private String status; // PENDING-待处理 APPROVED-已通过 REJECTED-已驳回
    
    @TableField("admin_note")
    private String adminNote;
    
    @TableField("credit_deduction")
    private Integer creditDeduction;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}
