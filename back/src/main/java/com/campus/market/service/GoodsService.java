package com.campus.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.dto.GoodsDTO;
import com.campus.market.dto.GoodsQueryDTO;
import com.campus.market.entity.Goods;
import com.campus.market.vo.GoodsVO;

public interface GoodsService extends IService<Goods> {
    Page<Goods> getGoodsList(GoodsQueryDTO queryDTO);
    Page<Goods> getMyGoodsList(Long userId, GoodsQueryDTO queryDTO);
    GoodsVO getGoodsDetail(Long id);
    void publishGoods(Long userId, GoodsDTO goodsDTO);
    void updateGoods(Long userId, Long id, GoodsDTO goodsDTO);
    void deleteGoods(Long userId, Long id);
    void updateGoodsStatus(Long userId, Long id, String status);
}
