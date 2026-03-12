package com.campus.market.controller;

import com.campus.market.common.Result;
import com.campus.market.dto.ReportDTO;
import com.campus.market.service.ReportService;
import com.campus.market.vo.ReportVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    @Resource
    private ReportService reportService;
    
    /**
     * 提交举报
     */
    @PostMapping("/submit")
    public Result<Void> submitReport(@RequestAttribute("userId") Long userId,
                                     @Valid @RequestBody ReportDTO dto) {
        reportService.submitReport(userId, dto);
        return Result.success();
    }
    
    /**
     * 获取我的举报记录
     */
    @GetMapping("/my")
    public Result<List<ReportVO>> getMyReports(@RequestAttribute("userId") Long userId) {
        List<ReportVO> reports = reportService.getMyReports(userId);
        return Result.success(reports);
    }
    
    /**
     * 获取举报列表（管理员）
     */
    @GetMapping("/list")
    public Result<List<ReportVO>> getReportList(@RequestParam(required = false) String status) {
        List<ReportVO> reports = reportService.getReportList(status);
        return Result.success(reports);
    }
    
    /**
     * 通过举报（管理员）
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approveReport(@PathVariable Long id,
                                      @RequestParam Integer creditDeduction,
                                      @RequestParam(required = false) String adminNote) {
        reportService.approveReport(id, creditDeduction, adminNote);
        return Result.success();
    }
    
    /**
     * 驳回举报（管理员）
     */
    @PutMapping("/{id}/reject")
    public Result<Void> rejectReport(@PathVariable Long id,
                                     @RequestParam String adminNote) {
        reportService.rejectReport(id, adminNote);
        return Result.success();
    }
}
