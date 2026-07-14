package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.service.TokenService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/token_handler.go。 */
@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Data
    public static class TokenAddParam {
        private String tokenDesc;
    }

    @PostMapping("/add")
    public Result<?> addToken(@RequestBody TokenAddParam param, @RequestAttribute("userId") Long userId) {
        if (param.getTokenDesc() == null) {
            return R.error(400, "参数错误");
        }
        try {
            return R.ok(tokenService.createToken(param.getTokenDesc(), userId));
        } catch (BizException e) {
            return R.error(9001, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteToken(@PathVariable Long id) {
        try {
            tokenService.deleteToken(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(9002, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageTokens(@RequestBody PageParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<TokenService.TokenDTO> p = tokenService.pageTokens(param);
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(9003, e.getMessage());
        }
    }
}
