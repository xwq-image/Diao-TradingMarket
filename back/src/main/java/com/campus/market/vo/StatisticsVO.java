package com.campus.market.vo;

import lombok.Data;
import java.util.Map;

@Data
public class StatisticsVO {
    // 用户统计
    private Integer totalUsers;
    private Integer newUsersLast7Days;
    
    // 商品统计
    private Integer totalGoods;
    private Integer publishedGoods;
    private Integer pendingGoods;
    private Map<String, Integer> categoryStats;
    
    // 订单统计
    private Integer totalOrders;
    private Integer completedOrders;
    private Double successRate;
}
