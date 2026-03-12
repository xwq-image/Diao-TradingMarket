package com.campus.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.Result;
import com.campus.market.dto.GoodsQueryDTO;
import com.campus.market.dto.LoginDTO;
import com.campus.market.dto.RegisterDTO;
import com.campus.market.entity.Goods;
import com.campus.market.entity.User;
import com.campus.market.service.GoodsService;
import com.campus.market.service.UserService;
import com.campus.market.vo.LoginVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;
    
    @Resource
    private GoodsService goodsService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestAttribute("userId") Long userId,
                                       @RequestBody User user) {
        userService.updateUserInfo(userId, user);
        return Result.success();
    }
    
    @GetMapping("/goods")
    public Result<Page<Goods>> getMyGoodsList(@RequestAttribute("userId") Long userId,
                                              GoodsQueryDTO queryDTO) {
        Page<Goods> page = goodsService.getMyGoodsList(userId, queryDTO);
        return Result.success(page);
    }
}
