package com.campus.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.Result;
import com.campus.market.entity.Goods;
import com.campus.market.entity.User;
import com.campus.market.service.AdminService;
import com.campus.market.vo.StatisticsVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    // 用户管理
    @GetMapping("/users")
    public Result<Page<User>> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String keyword) {
        Page<User> users = adminService.getUserList(page, size, keyword);
        return Result.success(users);
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                         @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success();
    }

    @PutMapping("/users/{id}/verify")
    public Result<Void> verifyUser(@PathVariable Long id,
                                   @RequestParam String status) {
        adminService.verifyUser(id, status);
        return Result.success();
    }

    // 商品审核
    @GetMapping("/goods/pending")
    public Result<Page<Goods>> getPendingGoods(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        Page<Goods> goods = adminService.getPendingGoods(page, size);
        return Result.success(goods);
    }

    @PutMapping("/goods/{id}/approve")
    public Result<Void> approveGoods(@PathVariable Long id) {
        adminService.approveGoods(id);
        return Result.success();
    }

    @PutMapping("/goods/{id}/reject")
    public Result<Void> rejectGoods(@PathVariable Long id,
                                    @RequestParam String reason) {
        adminService.rejectGoods(id, reason);
        return Result.success();
    }

    @PutMapping("/goods/{id}/offline")
    public Result<Void> offlineGoods(@PathVariable Long id,
                                     @RequestParam String reason) {
        adminService.offlineGoods(id, reason);
        return Result.success();
    }

    // 数据统计
    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO statistics = adminService.getStatistics();
        return Result.success(statistics);
    }
}
