package springboot.yongjunstore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springboot.yongjunstore.config.service.OAuth2UserService;
import springboot.yongjunstore.config.service.RefreshTokenService;
import springboot.yongjunstore.config.filter.JwtAuthenticationFilter;
import springboot.yongjunstore.config.filter.JwtExceptionFilter;
import springboot.yongjunstore.config.handler.Http401Handler;
import springboot.yongjunstore.config.handler.Http403Handler;
import springboot.yongjunstore.config.handler.OAuthenticationFailureHandler;
import springboot.yongjunstore.config.handler.OAuthenticationSuccessHandler;
import springboot.yongjunstore.config.jwt.JwtProvider;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.repository.MemberRepository;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityConfig {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final OAuth2UserService OAuth2UserService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/error");
                //.requestMatchers(toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/member/admin").access(new WebExpressionAuthorizationManager("hasRole('ROLE_MEMBER')"))
                        .anyRequest().permitAll()
                )

                .exceptionHandling(exception -> {
                    exception.accessDeniedHandler(new Http403Handler());
                    exception.authenticationEntryPoint(new Http401Handler());
                })

                .oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint.userService(new OAuth2UserService(memberRepository))
                    )
                    .failureHandler(new OAuthenticationFailureHandler())
                    .successHandler(new OAuthenticationSuccessHandler(jwtProvider, OAuth2UserService, refreshTokenService))
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
    public BCryptPasswordEncoder passwordEncoder(){
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
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}

