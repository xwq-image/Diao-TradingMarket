package com.campus.market.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.dto.ReportDTO;
import com.campus.market.entity.Report;
import com.campus.market.entity.User;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.ReportMapper;
import com.campus.market.mapper.UserMapper;
import com.campus.market.service.NotificationService;
import com.campus.market.service.ReportService;
import com.campus.market.vo.ReportVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    
    @Resource
    private ReportMapper reportMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public void submitReport(Long userId, ReportDTO dto) {
        // 不能举报自己
        if (userId.equals(dto.getReportedId())) {
            throw new BusinessException("不能举报自己");
        }
        
        // 检查被举报人是否存在
        User reported = userMapper.selectById(dto.getReportedId());
        if (reported == null) {
            throw new BusinessException("被举报人不存在");
        }
        
        Report report = new Report();
        BeanUtil.copyProperties(dto, report);
        report.setReporterId(userId);
        report.setStatus("PENDING");
        
        // 将证据图片列表转为JSON字符串
        if (dto.getEvidenceImages() != null && !dto.getEvidenceImages().isEmpty()) {
            report.setEvidenceImages(JSONUtil.toJsonStr(dto.getEvidenceImages()));
        }
        
        reportMapper.insert(report);
    }
    
    @Override
    public List<ReportVO> getMyReports(Long userId) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getReporterId, userId)
                .orderByDesc(Report::getCreateTime);
        
        List<Report> reports = reportMapper.selectList(wrapper);
        return convertToVOList(reports);
    }
    
    @Override
    public List<ReportVO> getReportList(String status) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Report::getStatus, status);
        }
        wrapper.orderByDesc(Report::getCreateTime);
        
        List<Report> reports = reportMapper.selectList(wrapper);
        return convertToVOList(reports);
    }
    
    @Override
    @Transactional
    public void approveReport(Long id, Integer creditDeduction, String adminNote) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        
        if (!"PENDING".equals(report.getStatus())) {
            throw new BusinessException("该举报已处理");
        }
        
        // 更新举报状态
        report.setStatus("APPROVED");
        report.setCreditDeduction(creditDeduction);
        report.setAdminNote(adminNote);
        reportMapper.updateById(report);
        
        // 扣除被举报人信誉分
        User reported = userMapper.selectById(report.getReportedId());
        int newCreditScore = Math.max(0, reported.getCreditScore() - creditDeduction);
        reported.setCreditScore(newCreditScore);
        userMapper.updateById(reported);
        
        // 通知被举报人
        notificationService.createNotification(
            report.getReportedId(),
            "report",
            "举报处理通知",
            "您被举报的行为已被确认，信誉分扣除" + creditDeduction + "分",
            id
        );
        
        // 通知举报人
        notificationService.createNotification(
            report.getReporterId(),
            "report",
            "举报处理通知",
            "您的举报已被受理",
            id
        );
    }
    
    @Override
    @Transactional
    public void rejectReport(Long id, String adminNote) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        
        if (!"PENDING".equals(report.getStatus())) {
            throw new BusinessException("该举报已处理");
        }
        
        // 更新举报状态
        report.setStatus("REJECTED");
        report.setAdminNote(adminNote);
        reportMapper.updateById(report);
        
        // 通知举报人
        notificationService.createNotification(
            report.getReporterId(),
            "report",
            "举报处理通知",
            "您的举报已被驳回：" + adminNote,
            id
        );
    }
    
    private List<ReportVO> convertToVOList(List<Report> reports) {
        List<ReportVO> result = new ArrayList<>();
        
        for (Report report : reports) {
            ReportVO vo = new ReportVO();
            BeanUtil.copyProperties(report, vo);
            
            // 获取举报人信息
            User reporter = userMapper.selectById(report.getReporterId());
            if (reporter != null) {
                vo.setReporterName(reporter.getNickname());
            }
            
            // 获取被举报人信息
            User reported = userMapper.selectById(report.getReportedId());
            if (reported != null) {
                vo.setReportedName(reported.getNickname());
            }
            
            // 解析证据图片JSON
            if (report.getEvidenceImages() != null && !report.getEvidenceImages().isEmpty()) {
                try {
                    vo.setEvidenceImages(JSONUtil.toList(report.getEvidenceImages(), String.class));
                } catch (Exception e) {
                    vo.setEvidenceImages(new ArrayList<>());
                }
            }
            
            result.add(vo);
        }
        
        return result;
    }
}
