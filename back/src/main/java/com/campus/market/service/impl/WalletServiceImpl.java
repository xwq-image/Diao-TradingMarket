package com.campus.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.entity.User;
import com.campus.market.entity.WalletTransaction;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.UserMapper;
import com.campus.market.mapper.WalletTransactionMapper;
import com.campus.market.service.WalletService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private WalletTransactionMapper walletTransactionMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public BigDecimal getBalance(Long userId) {
        User user = userMapper.selectById(userId);
        return user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional
    public void recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        
        User user = userMapper.selectById(userId);
        BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        // 更新余额
        user.setBalance(balanceAfter);
        userMapper.updateById(user);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("RECHARGE");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription("账户充值");
        walletTransactionMapper.insert(transaction);
    }
    
    @Override
    @Transactional
    public void pay(Long userId, Long orderId, BigDecimal amount, String password) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }
        
        User user = userMapper.selectById(userId);
        
        // 验证密码（使用BCrypt加密比较）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        
        // 检查余额
        if (balanceBefore.compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }
        
        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        
        // 更新余额
        user.setBalance(balanceAfter);
        userMapper.updateById(user);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("PAYMENT");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setOrderId(orderId);
        transaction.setDescription("订单支付");
        walletTransactionMapper.insert(transaction);
    }
    
    @Override
    @Transactional
    public void refund(Long userId, Long orderId, BigDecimal amount) {
        User user = userMapper.selectById(userId);
        BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        // 更新余额
        user.setBalance(balanceAfter);
        userMapper.updateById(user);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("REFUND");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setOrderId(orderId);
        transaction.setDescription("订单退款");
        walletTransactionMapper.insert(transaction);
    }
    
    @Override
    @Transactional
    public void income(Long userId, Long orderId, BigDecimal amount) {
        User user = userMapper.selectById(userId);
        BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        // 更新余额
        user.setBalance(balanceAfter);
        userMapper.updateById(user);
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("INCOME");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setOrderId(orderId);
        transaction.setDescription("订单收款");
        walletTransactionMapper.insert(transaction);
    }
    
    @Override
    public List<WalletTransaction> getTransactions(Long userId) {
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletTransaction::getUserId, userId)
                .orderByDesc(WalletTransaction::getCreateTime);
        return walletTransactionMapper.selectList(wrapper);
    }
}
