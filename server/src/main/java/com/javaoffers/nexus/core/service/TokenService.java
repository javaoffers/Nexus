package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Token;
import com.javaoffers.nexus.core.repository.TokenMapper;
import com.javaoffers.nexus.pkg.exception.BizException;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 对应 Go core/service/token_service.go。 */
@Service
public class TokenService {

    private final TokenMapper tokenMapper;

    public TokenService(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Data
    public static class TokenDTO {
        private Long id;
        private String tokenDesc;
        private Date createdAt;
    }

    public String createToken(String tokenDesc, Long userID) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("userId", userID);
        vo.put("timestamp", System.currentTimeMillis());
        String tokenValue;
        try {
            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(vo);
            tokenValue = Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BizException("生成Token失败");
        }
        Token t = new Token();
        t.setTokenValue(tokenValue);
        t.setTokenDesc(tokenDesc);
        t.setCreatedBy(userID);
        tokenMapper.create(t);
        return tokenValue;
    }

    public void deleteToken(Long id) {
        tokenMapper.delete(id);
    }

    public Page<TokenDTO> pageTokens(PageParam p) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<Token> list = tokenMapper.page(pn, ps);
        long total = tokenMapper.count();
        List<TokenDTO> dtos = new ArrayList<>();
        for (Token t : list) {
            TokenDTO dto = new TokenDTO();
            dto.setId(t.getId());
            dto.setTokenDesc(t.getTokenDesc());
            dto.setCreatedAt(t.getCreatedAt());
            dtos.add(dto);
        }
        return new Page<>(total, dtos);
    }
}
