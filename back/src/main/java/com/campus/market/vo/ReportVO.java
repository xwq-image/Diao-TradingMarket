package com.campus.market.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportVO {
    
    private Long id;
    
    private Long reporterId;
    
    private String reporterName;
    
    private Long reportedId;
    
    private String reportedName;
    
    private String type;
    
    private Long targetId;
    
    private String reason;
    
    private String description;
    
    private List<String> evidenceImages;
    
    private String status;
    
    private String adminNote;
    
    private Integer creditDeduction;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
