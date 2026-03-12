package com.campus.market.service;

import com.campus.market.dto.ReportDTO;
import com.campus.market.vo.ReportVO;

import java.util.List;

public interface ReportService {
    
    /**
     * 提交举报
     */
    void submitReport(Long userId, ReportDTO dto);
    
    /**
     * 获取我的举报记录
     */
    List<ReportVO> getMyReports(Long userId);
    
    /**
     * 获取举报列表（管理员）
     */
    List<ReportVO> getReportList(String status);
    
    /**
     * 通过举报（管理员）
     */
    void approveReport(Long id, Integer creditDeduction, String adminNote);
    
    /**
     * 驳回举报（管理员）
     */
    void rejectReport(Long id, String adminNote);
}
