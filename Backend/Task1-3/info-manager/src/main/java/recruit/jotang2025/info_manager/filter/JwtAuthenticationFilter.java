package recruit.jotang2025.info_manager.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;
import recruit.jotang2025.info_manager.utils.JwtUtils;

@Component
// 继承OncePerRequestFilter来确保每一次外部访问时, 这个Filter都只会被调用一次, 来减少资源的浪费
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // 已经发来的HTTP请求
            @NonNull HttpServletResponse response, // 即将发出的HTTP响应
            @NonNull FilterChain filterChain // 过滤器链对象, 当前工作结束时应当调用此对象的dofilter方法来放行请求
    ) throws ServletException, IOException // 提前声明这个方法可能会抛出的异常类型
    {
        // 获取请求头中的authorization
        String authHead = request.getHeader("Authorization");

        // 若没有获得到对应的token或者token格式错误, 直接放行
        // 因为没有Authentication对象被存入SecurityContextHolder, 所以放行之后Spring
        // Security会自动抛出AccessDeniedException
        // 因此不必担心直接放行会有什么坏结果
        if (authHead == null || !authHead.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从Header中提取JWT, 即Header中删去"Bearer "的部分
        String jwt = authHead.substring(7);

        try {
            // 获取载荷
            Claims claims = JwtUtils.parse(jwt);
            String role = claims.get("role", String.class);
            String userId = claims.get("sub", String.class);

            // 创建Authentication对象, 以便后续访问获取当前访问的用户的身份、权限信息
            Authentication authenticationToken = AuthenticationUtils.generateAuthentication(userId, role);

            // 将Authentication对象存入SecurityContextHolder, 这之后Spring
            // Security就有办法得知当前访问的用户的信息和权限了
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (JwtException e) {
            // 如果解析JWT期间抛出了异常, 那么在这里捕获这个异常, 以免它上浮被更高层捕获为未知异常
            // 不必担心直接放行会有坏结果, 原因也是因为如果抛出异常那么就不会有Authentication对象
            System.out.print("JWT解析异常! ");
        }

        // 放行
        filterChain.doFilter(request, response);
    }
}
