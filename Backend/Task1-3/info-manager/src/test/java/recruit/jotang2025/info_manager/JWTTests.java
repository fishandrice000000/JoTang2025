package recruit.jotang2025.info_manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import recruit.jotang2025.info_manager.utils.JwtUtils;

//@SpringBootTest
public class JWTTests {

    @Test
    void testGenJWT() {
        // 设置密钥
        String base64Secret = "chuangqianmingyueguangyishidishangshuangjutouwangmingyueditousiguxiang";
        SecretKey secretKey = Keys.hmacShaKeyFor(base64Secret.getBytes());

        // 设置过期时间
        Date now = new Date();
        Long expirationMills = now.getTime() + TimeUnit.HOURS.toMillis(12);
        Date expirationTime = new Date(expirationMills);

        // 设置自定义声明
        Map<String, Object> claims = new HashMap<>();
        String email = "123456@aaa.com";
        String password = "123456";
        claims.put("email", email);
        claims.put("password", password);

        // 构建JWT令牌
        String jwt = Jwts.builder()
                .header()
                .add("typ", "JWT") // 指定令牌类型
                .add("alg", "HS256") // 指定加密算法
                .and()

                .subject(email) // 指定令牌的主题, 一般是用户的唯一标识
                .issuer("ChrisWu") // 指定令牌的签发者
                .issuedAt(now) // 指定令牌的签发时间
                .expiration(expirationTime) // 指定令牌的过期时间

                .claims(claims) // 指定令牌的自定义声明

                .signWith(secretKey) // 指定签名的密钥

                .compact(); // 将令牌转换为字符串
        System.out.println(jwt);
    }

    @Test
    @Disabled
    void testParseJWT() {
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.Bearer .oWCWwfY_pBVGIYZUw2QhHDwkfuVUvY306gudeulhL-iuPT4s2q-haK-3hK5jS2XbwPZj7iTHDPcDaKtoUuvaHg";

        Claims jws = JwtUtils.parse(jwt);

        System.out.println(jws);
    }
}
