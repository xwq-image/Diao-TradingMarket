package com.campus.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.entity.User;
import com.campus.market.dto.LoginDTO;
import com.campus.market.dto.RegisterDTO;
import com.campus.market.vo.LoginVO;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
    User getUserInfo(Long userId);
    void updateUserInfo(Long userId, User user);
}
