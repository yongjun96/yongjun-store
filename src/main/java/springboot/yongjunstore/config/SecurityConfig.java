package springboot.yongjunstore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springboot.yongjunstore.config.filter.JwtAuthenticationFilter;
import springboot.yongjunstore.config.filter.JwtExceptionFilter;
import springboot.yongjunstore.config.handler.Http401Handler;
import springboot.yongjunstore.config.handler.Http403Handler;
import springboot.yongjunstore.config.handler.OAuthenticationFailureHandler;
import springboot.yongjunstore.config.handler.OAuthenticationSuccessHandler;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.config.service.CustomOAuth2UserService;
import springboot.yongjunstore.config.service.RefreshTokenService;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.repository.MemberRepository;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    @Value("${custom.url.frontend-url}")
    private String frontEndUrl;

    @Value("${custom.url.backend-url}")
    private String backEndUrl;


//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring().
//                //.requestMatchers("/favicon.ico")
//                //.requestMatchers("/error");
//        //.requestMatchers(toH2Console());
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/mail/**").access(new WebExpressionAuthorizationManager("hasRole('ROLE_MEMBER')"))
                        .requestMatchers("/member/**").access(new WebExpressionAuthorizationManager("hasRole('ROLE_MEMBER')"))
                        //.requestMatchers("/roomPost/create*").access(new WebExpressionAuthorizationManager("hasRole('ROLE_MEMBER')"))
                        .requestMatchers("/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().permitAll()
                )

                .exceptionHandling(exception -> {
                    exception.accessDeniedHandler(new Http403Handler());
                    exception.authenticationEntryPoint(new Http401Handler());
                })

                .oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(new CustomOAuth2UserService(memberRepository, defaultOAuth2UserService()))
                                )
                                .failureHandler(new OAuthenticationFailureHandler(frontEndUrl))
                                .successHandler(new OAuthenticationSuccessHandler(frontEndUrl, jwtProvider, memberRepository, refreshTokenService))
                )


                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(jwtProvider, refreshTokenService), JwtAuthenticationFilter.class)

                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }


    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {

        return username -> {
            Member member = memberRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username + "을 찾을 수 없습니다."));

            return new UserPrincipal(member);
        };
    }

    @Bean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService(memberRepository));
        provider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(provider);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(frontEndUrl);
        config.addAllowedOrigin(backEndUrl);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}

