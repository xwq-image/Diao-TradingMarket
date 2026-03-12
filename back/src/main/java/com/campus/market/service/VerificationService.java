package com.campus.market.service;

import com.campus.market.dto.VerificationDTO;
import com.campus.market.vo.VerificationVO;

import java.util.List;

public interface VerificationService {
    
    /**
     * 提交认证申请
     */
    void applyVerification(Long userId, VerificationDTO dto);
    
    /**
     * 获取我的认证信息
     */
    VerificationVO getMyVerification(Long userId);
    
    /**
     * 获取认证列表（管理员）
     */
    List<VerificationVO> getVerificationList(String status);
    
    /**
     * 通过认证（管理员）
     */
    void approveVerification(Long id);
    
    /**
     * 驳回认证（管理员）
     */
    void rejectVerification(Long id, String reason);
}
