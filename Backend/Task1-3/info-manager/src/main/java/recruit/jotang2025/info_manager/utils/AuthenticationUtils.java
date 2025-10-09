package recruit.jotang2025.info_manager.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {
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
