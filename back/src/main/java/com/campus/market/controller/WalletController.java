package com.campus.market.controller;

import com.campus.market.common.Result;
import com.campus.market.entity.WalletTransaction;
import com.campus.market.service.WalletService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    
    @Resource
    private WalletService walletService;
    
    /**
     * 获取余额
     */
    @GetMapping("/balance")
    public Result<BigDecimal> getBalance(@RequestAttribute("userId") Long userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return Result.success(balance);
    }
    
    /**
     * 充值
     */
    @PostMapping("/recharge")
    public Result<Void> recharge(@RequestAttribute("userId") Long userId,
                                 @RequestParam BigDecimal amount) {
        walletService.recharge(userId, amount);
        return Result.success();
    }
    
    /**
     * 获取交易记录
     */
    @GetMapping("/transactions")
    public Result<List<WalletTransaction>> getTransactions(@RequestAttribute("userId") Long userId) {
        List<WalletTransaction> transactions = walletService.getTransactions(userId);
        return Result.success(transactions);
    }
}
