package com.campus.market.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.market.dto.LoginDTO;
import com.campus.market.dto.RegisterDTO;
import com.campus.market.entity.User;
import com.campus.market.exception.BusinessException;
import com.campus.market.mapper.UserMapper;
import com.campus.market.service.UserService;
import com.campus.market.util.JwtUtil;
import com.campus.market.vo.LoginVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, loginDTO.getPhone()));
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRole(user.getRole());
        
        return loginVO;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if (this.count(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, registerDTO.getPhone())) > 0) {
            throw new BusinessException("手机号已注册");
        }
        
        if (this.count(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, registerDTO.getStudentId())) > 0) {
            throw new BusinessException("学号已注册");
        }
        
        User user = new User();
        user.setStudentId(registerDTO.getStudentId());
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setCreditScore(100);
        user.setRole("user");
        user.setStatus(1);
        user.setVerified(0); // 0 表示未认证
        
        this.save(user);
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(Long userId, User user) {
        User existUser = this.getById(userId);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (StrUtil.isNotBlank(user.getNickname())) {
            existUser.setNickname(user.getNickname());
        }
        if (StrUtil.isNotBlank(user.getCampus())) {
            existUser.setCampus(user.getCampus());
        }
        if (StrUtil.isNotBlank(user.getDormitory())) {
            existUser.setDormitory(user.getDormitory());
        }
        
        this.updateById(existUser);
    }
}
