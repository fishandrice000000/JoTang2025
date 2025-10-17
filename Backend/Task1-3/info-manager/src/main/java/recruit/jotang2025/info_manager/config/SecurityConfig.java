package recruit.jotang2025.info_manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import recruit.jotang2025.info_manager.filter.JwtAuthenticationFilter;

@Configuration // 配置类的统一注解
@EnableWebSecurity // 启用Spring Security
@EnableMethodSecurity // 启用@PreAuthorize等在方法层面鉴权的注解
public class SecurityConfig {

    // BCrypt加密的配置
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 主要内容, 子类SecurityFilterChain包含了细节的配置内容
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF防护
                // CSRF是针对有状态的应用的攻击, 而这里使用的JWT是无状态的认证, 所以不需要开启CSRF
                // 将其关闭反而在一些时候会更加便利
                .csrf(csrf -> csrf.disable())

                // 将Session管理设置成无状态
                // 因为使用了JWT, 后端只需要获取请求头的中的JWT就可以得知用户的信息
                // 所以不需要再开启Session了
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置URL的访问权限
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/user/register", "/").permitAll() // 访问"/login", "/"的请求总是被允许
                        .anyRequest().authenticated() // 除此之外的请求都应当拥有授权
                )

                // 将自制Filter正式加入到FilterChain中
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // 使用build()将HttpSecurity对象的变量http打包成SecurityFilterChain对象
    }

}
