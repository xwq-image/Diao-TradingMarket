package com.campus.market.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.entity.Goods;
import com.campus.market.entity.Order;
import com.campus.market.entity.User;
import com.campus.market.exception.BusinessException;
import com.campus.market.service.AdminService;
import com.campus.market.service.GoodsService;
import com.campus.market.service.OrderService;
import com.campus.market.service.UserService;
import com.campus.market.vo.StatisticsVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserService userService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private OrderService orderService;

    @Override
    public Page<User> getUserList(Integer page, Integer size, String keyword) {
        Page<User> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getStudentId, keyword));
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return userService.page(pageObj, wrapper);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        userService.updateById(user);
    }

    @Override
    public void verifyUser(Long userId, String status) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 将 status 转换为 Integer: "approved" -> 1, 其他 -> 0
        user.setVerified("approved".equals(status) ? 1 : 0);
        userService.updateById(user);
    }

    @Override
    public Page<Goods> getPendingGoods(Integer page, Integer size) {
        Page<Goods> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getStatus, "pending")
                .orderByAsc(Goods::getCreateTime);
        return goodsService.page(pageObj, wrapper);
    }

    @Override
    public void approveGoods(Long goodsId) {
        Goods goods = goodsService.getById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        goods.setStatus("published");
        goods.setRejectReason(null);
        goodsService.updateById(goods);
    }

    @Override
    public void rejectGoods(Long goodsId, String reason) {
        Goods goods = goodsService.getById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        goods.setStatus("offline");
        goods.setRejectReason(reason);
        goodsService.updateById(goods);
    }

    @Override
    public void offlineGoods(Long goodsId, String reason) {
        Goods goods = goodsService.getById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        goods.setStatus("offline");
        goods.setRejectReason(reason);
        goodsService.updateById(goods);
    }

    @Override
    public StatisticsVO getStatistics() {
        StatisticsVO vo = new StatisticsVO();
        
        // 用户统计
        vo.setTotalUsers(Math.toIntExact(userService.count()));
        
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.ge(User::getCreateTime, sevenDaysAgo);
        vo.setNewUsersLast7Days(Math.toIntExact(userService.count(userWrapper)));
        
        // 商品统计
        vo.setTotalGoods(Math.toIntExact(goodsService.count()));
        
        LambdaQueryWrapper<Goods> publishedWrapper = new LambdaQueryWrapper<>();
        publishedWrapper.eq(Goods::getStatus, "published");
        vo.setPublishedGoods(Math.toIntExact(goodsService.count(publishedWrapper)));
        
        LambdaQueryWrapper<Goods> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Goods::getStatus, "pending");
        vo.setPendingGoods(Math.toIntExact(goodsService.count(pendingWrapper)));
        
        // 商品分类统计
        Map<String, Integer> categoryStats = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Goods::getCategoryId, i);
            int count = Math.toIntExact(goodsService.count(wrapper));
            categoryStats.put(getCategoryName(i), count);
        }
        vo.setCategoryStats(categoryStats);
        
        // 订单统计
        vo.setTotalOrders(Math.toIntExact(orderService.count()));
        
        LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Order::getStatus, "completed");
        int completedOrders = Math.toIntExact(orderService.count(completedWrapper));
        vo.setCompletedOrders(completedOrders);
        
        // 交易成功率
        if (vo.getTotalOrders() > 0) {
            vo.setSuccessRate((double) completedOrders / vo.getTotalOrders() * 100);
        } else {
            vo.setSuccessRate(0.0);
        }
        
        return vo;
    }

    private String getCategoryName(int categoryId) {
        switch (categoryId) {
            case 1: return "教材书籍";
            case 2: return "电子数码";
            case 3: return "生活用品";
            case 4: return "交通工具";
            case 5: return "其他";
            default: return "未知";
        }
    }
}
