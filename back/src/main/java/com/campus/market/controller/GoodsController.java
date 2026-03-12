package com.campus.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.Result;
import com.campus.market.dto.GoodsDTO;
import com.campus.market.dto.GoodsQueryDTO;
import com.campus.market.entity.Goods;
import com.campus.market.service.GoodsService;
import com.campus.market.vo.GoodsVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @GetMapping("/list")
    public Result<Page<Goods>> getGoodsList(GoodsQueryDTO queryDTO) {
        Page<Goods> page = goodsService.getGoodsList(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<GoodsVO> getGoodsDetail(@PathVariable Long id) {
        GoodsVO goodsVO = goodsService.getGoodsDetail(id);
        return Result.success(goodsVO);
    }

    @PostMapping("/publish")
    public Result<Void> publishGoods(@RequestAttribute("userId") Long userId,
                                     @Valid @RequestBody GoodsDTO goodsDTO) {
        goodsService.publishGoods(userId, goodsDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateGoods(@RequestAttribute("userId") Long userId,
                                    @PathVariable Long id,
                                    @Valid @RequestBody GoodsDTO goodsDTO) {
        goodsService.updateGoods(userId, id, goodsDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGoods(@RequestAttribute("userId") Long userId,
                                    @PathVariable Long id) {
        goodsService.deleteGoods(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateGoodsStatus(@RequestAttribute("userId") Long userId,
                                          @PathVariable Long id,
                                          @RequestParam String status) {
        goodsService.updateGoodsStatus(userId, id, status);
        return Result.success();
    }
}
