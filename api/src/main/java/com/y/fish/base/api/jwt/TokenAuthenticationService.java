package com.y.fish.base.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by myliang on 7/21/17.
 */
public class TokenAuthenticationService {

    static final long EXPIRATION_TIME = 432_000_000;     // 5天

    static final String SECRET = "123!@#$%^PLM";            // JWT密码

    static final String TOKEN_PREFIX = "Bearer ";        // Token前缀

    static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

    // JWT生成方法
    static void addAuthentication(HttpServletResponse response, String username) {

        // 生成JWT
        String JWT = Jwts.builder()
                // 保存权限（角色）
                .claim("authorities", "READ")
                // 用户名写入标题
                .setSubject(username)
                // 有效期设置
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // 签名设置
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        // 将 JWT 写入 body
        try {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().print("{\"token\":\"" + JWT + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JWT验证方法
    static Authentication getAuthentication(HttpServletRequest request) {
        // 从Header中拿到token
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            try {
                // 解析 Token
                Claims claims = Jwts.parser()
                        // 验签
                        .setSigningKey(SECRET)
                        // 去掉 Bearer
                        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                        .getBody();

                // 拿用户名
                String user = claims.getSubject();

                // 得到 权限（角色）
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

                // 返回验证令牌
                return user != null ?
                        new UsernamePasswordAuthenticationToken(user, null, authorities) :
                        null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
