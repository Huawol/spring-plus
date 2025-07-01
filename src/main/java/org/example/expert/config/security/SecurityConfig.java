package org.example.expert.config.security;


import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtFilter;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(Customizer.withDefaults()) //Cross-Origin Resource Sharing // 프론트엔드랑 협업할때 주로 사용
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session // session 안쓰겠다고 알리는거
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // config로 url에 대한 인증/인가를 관리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup").permitAll()
                        .requestMatchers("/auth/signin").permitAll()
                        .requestMatchers("/todos").hasAnyRole(UserRole.USER.name(), UserRole.ADMIN.name()) // 유저랑 어드민 둘다 허용
                        .requestMatchers("/todos/**").hasAnyRole(UserRole.USER.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/admin/users/").hasRole(UserRole.ADMIN.name())
                        .anyRequest().denyAll()
                )

                //필터 등록
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                // 에러 처리까지 완벽하게 하고 싶다면 exceptionHandling 을 등록을 해야한다.
                .exceptionHandling(configurer ->
                        configurer
                                .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증할때 발생하는 오류를 처리하는...
                                .accessDeniedHandler(customAccessDeniedHandler) // 인가처리할때 발생하는 오류를 처리하는 ...
                )
                .build();
    }
}
