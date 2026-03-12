package com.campus.market.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.entity.Favorite;
import com.campus.market.entity.Goods;
import com.campus.market.entity.User;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.FavoriteMapper;
import com.campus.market.service.FavoriteService;
import com.campus.market.service.GoodsService;
import com.campus.market.service.UserService;
import com.campus.market.vo.GoodsVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    private GoodsService goodsService;

    @Resource
    private UserService userService;

    @Override
    public void addFavorite(Long userId, Long goodsId) {
        Goods goods = goodsService.getById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId);

        if (this.count(wrapper) > 0) {
            throw new BusinessException("已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        this.save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long goodsId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId);
        this.remove(wrapper);
    }

    @Override
    public boolean isFavorite(Long userId, Long goodsId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<GoodsVO> getMyFavorites(Long userId, Integer page, Integer size) {
        Page<Favorite> favoritePage = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> favorites = this.page(favoritePage, wrapper);

        // 转换为GoodsVO
        Page<GoodsVO> result = new Page<>(page, size);
        result.setTotal(favorites.getTotal());

        List<GoodsVO> goodsVOList = new ArrayList<>();
        for (Favorite favorite : favorites.getRecords()) {
            Goods goods = goodsService.getById(favorite.getGoodsId());
            if (goods != null) {
                GoodsVO vo = new GoodsVO();
                BeanUtil.copyProperties(goods, vo);
                vo.setIsFavorite(true);

                // 获取用户信息
                User user = userService.getById(goods.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname());
                    vo.setUserAvatar(user.getAvatar());
                }

                goodsVOList.add(vo);
            }
        }

        result.setRecords(goodsVOList);
        return result;
    }
}
