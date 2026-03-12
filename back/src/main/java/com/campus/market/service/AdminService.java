package com.campus.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.entity.Goods;
import com.campus.market.entity.User;
import com.campus.market.vo.StatisticsVO;

public interface AdminService {
    Page<User> getUserList(Integer page, Integer size, String keyword);
    void updateUserStatus(Long userId, Integer status);
    void verifyUser(Long userId, String status);
    
    Page<Goods> getPendingGoods(Integer page, Integer size);
    void approveGoods(Long goodsId);
    void rejectGoods(Long goodsId, String reason);
    void offlineGoods(Long goodsId, String reason);
    
    StatisticsVO getStatistics();
}
