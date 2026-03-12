package com.campus.market.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.dto.VerificationDTO;
import com.campus.market.entity.User;
import com.campus.market.entity.Verification;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.UserMapper;
import com.campus.market.mapper.VerificationMapper;
import com.campus.market.service.VerificationService;
import com.campus.market.vo.VerificationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class VerificationServiceImpl implements VerificationService {
    
    @Resource
    private VerificationMapper verificationMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public void applyVerification(Long userId, VerificationDTO dto) {
        // 检查是否已认证
        User user = userMapper.selectById(userId);
        if (user.getVerified() == 1) {
            throw new BusinessException("您已经通过认证");
        }
        
        // 检查是否有待审核的申请
        LambdaQueryWrapper<Verification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Verification::getUserId, userId)
                .eq(Verification::getStatus, "PENDING");
        Verification pending = verificationMapper.selectOne(wrapper);
        if (pending != null) {
            throw new BusinessException("您有待审核的认证申请，请等待审核");
        }
        
        // 创建认证申请
        Verification verification = new Verification();
        BeanUtil.copyProperties(dto, verification);
        verification.setUserId(userId);
        verification.setStatus("PENDING");
        verificationMapper.insert(verification);
    }
    
    @Override
    public VerificationVO getMyVerification(Long userId) {
        LambdaQueryWrapper<Verification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Verification::getUserId, userId)
                .orderByDesc(Verification::getCreateTime)
                .last("LIMIT 1");
        
        Verification verification = verificationMapper.selectOne(wrapper);
        if (verification == null) {
            return null;
        }
        
        VerificationVO vo = new VerificationVO();
        BeanUtil.copyProperties(verification, vo);
        
        User user = userMapper.selectById(userId);
        vo.setUserName(user.getNickname());
        
        return vo;
    }
    
    @Override
    public List<VerificationVO> getVerificationList(String status) {
        LambdaQueryWrapper<Verification> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Verification::getStatus, status);
        }
        wrapper.orderByDesc(Verification::getCreateTime);
        
        List<Verification> verifications = verificationMapper.selectList(wrapper);
        List<VerificationVO> result = new ArrayList<>();
        
        for (Verification verification : verifications) {
            VerificationVO vo = new VerificationVO();
            BeanUtil.copyProperties(verification, vo);
            
            User user = userMapper.selectById(verification.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public void approveVerification(Long id) {
        Verification verification = verificationMapper.selectById(id);
        if (verification == null) {
            throw new BusinessException("认证申请不存在");
        }
        
        if (!"PENDING".equals(verification.getStatus())) {
            throw new BusinessException("该申请已处理");
        }
        
        // 更新认证状态
        verification.setStatus("APPROVED");
        verificationMapper.updateById(verification);
        
        // 更新用户认证状态
        User user = userMapper.selectById(verification.getUserId());
        user.setVerified(1);
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void rejectVerification(Long id, String reason) {
        Verification verification = verificationMapper.selectById(id);
        if (verification == null) {
            throw new BusinessException("认证申请不存在");
        }
        
        if (!"PENDING".equals(verification.getStatus())) {
            throw new BusinessException("该申请已处理");
        }
        
        // 更新认证状态
        verification.setStatus("REJECTED");
        verification.setRejectReason(reason);
        verificationMapper.updateById(verification);
    }
}
