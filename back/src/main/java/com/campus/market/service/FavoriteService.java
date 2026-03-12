package com.campus.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.entity.Favorite;
import com.campus.market.vo.GoodsVO;

public interface FavoriteService extends IService<Favorite> {
    void addFavorite(Long userId, Long goodsId);
    void removeFavorite(Long userId, Long goodsId);
    boolean isFavorite(Long userId, Long goodsId);
    Page<GoodsVO> getMyFavorites(Long userId, Integer page, Integer size);
}
