package recruit.jotang2025.info_manager.utils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {
    final private static String KEY_STRING = "recruit.jotang.2025.info_manager";
    final private static SecretKey KEY = Keys.hmacShaKeyFor(KEY_STRING.getBytes());

    // 生成JWT
    public static String generate(Map<String, ?> claims, String subject, Long lifeTime) {
        
        Date now = new Date();
        Long expirationMills = now.getTime() + TimeUnit.HOURS.toMillis(lifeTime);
        Date expiratioTime = new Date(expirationMills);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("2025JoTang")
                .issuedAt(now)
                .expiration(expiratioTime)

                .signWith(KEY)
                .compact();
    }

    // 解析JWT
    public static Claims parse(String jwt) {
        String keyString = "recruit.jotang.2025.info_manager";
        SecretKey key = Keys.hmacShaKeyFor(keyString.getBytes());
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt);
        return jws.getPayload();
    }
}
