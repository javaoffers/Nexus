package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.service.UserService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/user_handler.go。 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Data
    public static class LoginParam {
        private String userName;
        private String password;
    }

    @Data
    public static class UpdatePasswordParam {
        private Long userId;
        private String oldPassword;
        private String newPassword;
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginParam param) {
        if (param.getUserName() == null || param.getPassword() == null) {
            return R.error(400, "参数错误");
        }
        try {
            return R.ok(userService.login(param.getUserName(), param.getPassword()));
        } catch (BizException e) {
            return R.error(1001, e.getMessage());
        }
    }

    @GetMapping("/product/info")
    public Result<?> getProductInfo() {
        return R.ok("v1.5.0");
    }

    @PostMapping("/getUserInfo")
    public Result<?> getUserInfo(@RequestAttribute("userId") Long userId) {
        try {
            return R.ok(userService.getUserInfo(userId));
        } catch (BizException e) {
            return R.error(1002, e.getMessage());
        }
    }

    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody UpdatePasswordParam param,
                                     @RequestAttribute("userId") Long userId) {
        if (param.getOldPassword() == null || param.getNewPassword() == null) {
            return R.error(400, "参数错误");
        }
        Long uid = (param.getUserId() != null && param.getUserId() > 0) ? param.getUserId() : userId;
        try {
            userService.updatePassword(uid, param.getOldPassword(), param.getNewPassword());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(1003, e.getMessage());
        }
    }
}
