package com.campus.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private String studentId;
    
    private String nickname;
    private String phone;
    private String password;
    private String avatar;
    private String campus;
    private String dormitory;
    
    @TableField("credit_score")
    private Integer creditScore;
    
    private String role;
    
    private Integer status;
    
    @TableField("is_verified")
    private Integer verified;
    
    private BigDecimal balance;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 手动添加 getter/setter 方法
    public Integer getVerified() {
        return verified;
    }
    
    public void setVerified(Integer verified) {
        this.verified = verified;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
