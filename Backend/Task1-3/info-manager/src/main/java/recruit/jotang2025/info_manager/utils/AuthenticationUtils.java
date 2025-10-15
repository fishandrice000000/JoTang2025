package recruit.jotang2025.info_manager.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {
    // 生成一个认证信息
    public static Authentication generateAuthentication(String principal, String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority a = new SimpleGrantedAuthority("ROLE_" + role);
        authorities.add(a);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                principal, // Principal: 主体, 当前用户的身份标识
                null, // Crdentials: 凭证, 当前用户的验证凭证, 一般是密码; 但是在使用JWT时, 通常是先通过凭证来获得JWT, 所以在这里凭证就没有必要了,
                      // 所以设置为null
                authorities // Authorities: 权限, 当前用户的权限
        );
        return authenticationToken;
    }

    // 设置当前访问用户的认证信息
    public static void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 获得当前访问用户的认证信息
    public static Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("用户未认证！"); // 谨慎一手再验证一下
        }
        return auth;
    }

    // 获得当前访问用户的userId
    public static String getCurrentUserId() {
        Authentication auth = getAuthentication();
        return auth.getPrincipal().toString();
    }

    // 判断当前访问用户身份是否为user
    public static boolean currentRoleIsUser() {
        // 获得当前访问用户的认证信息
        Authentication auth = getAuthentication();
        Boolean isUser = auth // 验证当前用户是否是admin, 也就是验证Authentication的Authorities是否含有ROLE_admin字段
                .getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_user"));
        return isUser;
    }
}
