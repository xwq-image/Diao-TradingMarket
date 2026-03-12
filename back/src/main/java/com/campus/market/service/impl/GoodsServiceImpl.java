package com.campus.market.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.dto.GoodsDTO;
import com.campus.market.dto.GoodsQueryDTO;
import com.campus.market.entity.Goods;
import com.campus.market.entity.User;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.GoodsMapper;
import com.campus.market.mapper.UserMapper;
import com.campus.market.service.GoodsService;
import com.campus.market.vo.GoodsVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<Goods> getGoodsList(GoodsQueryDTO queryDTO) {
        Page<Goods> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getStatus, "published");
        
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.like(Goods::getTitle, queryDTO.getKeyword());
        }
        
        if (StrUtil.isNotBlank(queryDTO.getCategory())) {
            wrapper.eq(Goods::getCategoryId, queryDTO.getCategory());
        }
        
        if (queryDTO.getMinPrice() != null) {
            wrapper.ge(Goods::getCurrentPrice, queryDTO.getMinPrice());
        }
        
        if (queryDTO.getMaxPrice() != null) {
            wrapper.le(Goods::getCurrentPrice, queryDTO.getMaxPrice());
        }
        
        if (StrUtil.isNotBlank(queryDTO.getCondition())) {
            wrapper.eq(Goods::getCondition, queryDTO.getCondition());
        }
        
        switch (queryDTO.getSortBy()) {
            case "price_asc":
                wrapper.orderByAsc(Goods::getCurrentPrice);
                break;
            case "price_desc":
                wrapper.orderByDesc(Goods::getCurrentPrice);
                break;
            case "views":
                wrapper.orderByDesc(Goods::getViews);
                break;
            default:
                wrapper.orderByDesc(Goods::getCreateTime);
        }
        
        return this.page(page, wrapper);
    }

    @Override
    public Page<Goods> getMyGoodsList(Long userId, GoodsQueryDTO queryDTO) {
        Page<Goods> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        // 只查询当前用户的商品
        wrapper.eq(Goods::getUserId, userId);
        
        // 如果指定了状态，则筛选状态
        if (StrUtil.isNotBlank(queryDTO.getStatus())) {
            wrapper.eq(Goods::getStatus, queryDTO.getStatus());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Goods::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public GoodsVO getGoodsDetail(Long id) {
        Goods goods = this.getById(id);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 增加浏览次数
        goods.setViews(goods.getViews() + 1);
        this.updateById(goods);
        
        // 获取卖家信息
        User seller = userMapper.selectById(goods.getUserId());
        
        // 构建VO
        GoodsVO goodsVO = new GoodsVO();
        BeanUtil.copyProperties(goods, goodsVO);
        if (seller != null) {
            goodsVO.setUserName(seller.getNickname());
            goodsVO.setUserAvatar(seller.getAvatar());
            goodsVO.setUserCreditScore(seller.getCreditScore());
        }
        
        return goodsVO;
    }

    @Override
    public void publishGoods(Long userId, GoodsDTO goodsDTO) {
        // 检查用户是否已认证（管理员无需认证）
        User user = userMapper.selectById(userId);
        if (!"admin".equals(user.getRole()) && (user.getVerified() == null || user.getVerified() == 0)) {
            throw new BusinessException("请先完成实名认证后再发布商品");
        }
        
        Goods goods = new Goods();
        BeanUtil.copyProperties(goodsDTO, goods);
        goods.setUserId(userId);
        goods.setStatus("pending");
        goods.setViews(0);
        
        this.save(goods);
    }

    @Override
    public void updateGoods(Long userId, Long id, GoodsDTO goodsDTO) {
        Goods goods = this.getById(id);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (!goods.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        if ("sold".equals(goods.getStatus())) {
            throw new BusinessException("已售出商品不可修改");
        }
        
        BeanUtil.copyProperties(goodsDTO, goods);
        this.updateById(goods);
    }

    @Override
    public void deleteGoods(Long userId, Long id) {
        Goods goods = this.getById(id);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (!goods.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        this.removeById(id);
    }

    @Override
    public void updateGoodsStatus(Long userId, Long id, String status) {
        Goods goods = this.getById(id);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (!goods.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        goods.setStatus(status);
        this.updateById(goods);
    }
}
