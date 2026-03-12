package com.campus.market.service;

import com.campus.market.entity.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    
    /**
     * 获取余额
     */
    BigDecimal getBalance(Long userId);
    
    /**
     * 充值
     */
    void recharge(Long userId, BigDecimal amount);
    
    /**
     * 支付（需要验证密码）
     */
    void pay(Long userId, Long orderId, BigDecimal amount, String password);
    
    /**
     * 退款
     */
    void refund(Long userId, Long orderId, BigDecimal amount);
    
    /**
     * 收入（卖家收款）
     */
    void income(Long userId, Long orderId, BigDecimal amount);
    
    /**
     * 获取交易记录
     */
    List<WalletTransaction> getTransactions(Long userId);
}
