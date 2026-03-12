package com.campus.market.controller;

import com.campus.market.common.Result;
import com.campus.market.dto.VerificationDTO;
import com.campus.market.service.VerificationService;
import com.campus.market.vo.VerificationVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/verification")
public class VerificationController {
    
    @Resource
    private VerificationService verificationService;
    
    /**
     * 提交认证申请
     */
    @PostMapping("/apply")
    public Result<Void> applyVerification(@RequestAttribute("userId") Long userId,
                                          @Valid @RequestBody VerificationDTO dto) {
        verificationService.applyVerification(userId, dto);
        return Result.success();
    }
    
    /**
     * 获取我的认证信息
     */
    @GetMapping("/my")
    public Result<VerificationVO> getMyVerification(@RequestAttribute("userId") Long userId) {
        VerificationVO vo = verificationService.getMyVerification(userId);
        return Result.success(vo);
    }
    
    /**
     * 获取认证列表（管理员）
     */
    @GetMapping("/list")
    public Result<List<VerificationVO>> getVerificationList(@RequestParam(required = false) String status) {
        List<VerificationVO> list = verificationService.getVerificationList(status);
        return Result.success(list);
    }
    
    /**
     * 通过认证（管理员）
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approveVerification(@PathVariable Long id) {
        verificationService.approveVerification(id);
        return Result.success();
    }
    
    /**
     * 驳回认证（管理员）
     */
    @PutMapping("/{id}/reject")
    public Result<Void> rejectVerification(@PathVariable Long id, @RequestParam String reason) {
        verificationService.rejectVerification(id, reason);
        return Result.success();
    }
}
