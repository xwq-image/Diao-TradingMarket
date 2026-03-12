package com.campus.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.Result;
import com.campus.market.service.FavoriteService;
import com.campus.market.vo.GoodsVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public Result<Void> addFavorite(@RequestAttribute("userId") Long userId,
                                    @RequestParam Long goodsId) {
        favoriteService.addFavorite(userId, goodsId);
        return Result.success();
    }

    @DeleteMapping("/remove")
    public Result<Void> removeFavorite(@RequestAttribute("userId") Long userId,
                                       @RequestParam Long goodsId) {
        favoriteService.removeFavorite(userId, goodsId);
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Boolean> isFavorite(@RequestAttribute("userId") Long userId,
                                      @RequestParam Long goodsId) {
        boolean isFavorite = favoriteService.isFavorite(userId, goodsId);
        return Result.success(isFavorite);
    }

    @GetMapping("/my")
    public Result<Page<GoodsVO>> getMyFavorites(@RequestAttribute("userId") Long userId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        Page<GoodsVO> favorites = favoriteService.getMyFavorites(userId, page, size);
        return Result.success(favorites);
    }
}
