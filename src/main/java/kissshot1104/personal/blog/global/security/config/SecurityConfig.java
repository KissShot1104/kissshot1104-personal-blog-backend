package kissshot1104.personal.blog.global.security.config;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 추가된 코드
        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.build();

        //직접 구현한 로그인 필터를 등록할 수있도록 함
        http.authenticationManager(authenticationManager);

        http
                .csrf(AbstractHttpConfigurer::disable)//csrf 비활성화
//                .cors().and()//cors커스텀설정가능
                .formLogin(AbstractHttpConfigurer::disable)//인증 방식을 커스텀하기 위해 formLogin을 비활성화 한다.
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**"))
                        .permitAll())//권한이 없을경우 접속할 수 있는 한계선 설정(지금 전부 허가)
                .addFilterAt(//우리가 만든 로그인 요청 필터가  동작하도록 하기 위해 필터를 바꿔치기함
                        this.abstractAuthenticationProcessingFilter(
                                authenticationManager,
                                authenticationSuccessHandler()),//로그인 성공 시 작동
                        UsernamePasswordAuthenticationFilter.class)
                .logout(logoutConfig ->
                        logoutConfig
                                .logoutUrl("/member/logout")//해당 url을 요청 시 로그아웃 동작
                                .logoutSuccessHandler(//로그아웃이 될 시 실행될 메서드 등록
                                        ((request, response, authentication) -> {
                                            System.out.println("로그아웃 성공");

                                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());//응답데이터 등록
                                            response.setContentType(
                                                    MediaType.APPLICATION_JSON_VALUE);  //응답 컨텐츠를 json으로 응갑함
                                            response.getWriter().println("로그아웃 성공!");   //요청자에게 전송
                                        })
                                ))
                .headers(
                        headers ->
                                headers.frameOptions(
                                        frameOptions ->
                                                frameOptions.sameOrigin()
                                )
                );
//                .headers(
//                        headersConfigurer ->
//                                headersConfigurer
//                                        .frameOptions(
//                                                //프레임 내의 로딩을 제한하고 공격자 페이지로 리다이렉션등을 방지하여 클릭재킹 공격 방지한다.
//                                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
//                                        )
//                                        .contentSecurityPolicy(policyConfig ->//스크립트, 이미지, 폰트, 기본, 프레임소스를 제한하여 xss공격제한
//                                                policyConfig.policyDirectives(//default(모든컨텐츠적용)만 해도 동작은 함
//                                                        "script-src 'self'; " +
//                                                                "font-src 'self' data:; " + "default-src 'self'; " +
//                                                                "frame-src 'self'"
//                                                )
//                                        )
//                );

        return http.build();
    }

    //
    public AbstractAuthenticationProcessingFilter abstractAuthenticationProcessingFilter(
            final AuthenticationManager authenticationManager,
            final AuthenticationSuccessHandler authenticationSuccessHandler
    ) {
        // "/member/login"을 요청했을 경우 필터가 동작함
        return new LoginAuthenticationFilter(
                "/member/login",
                authenticationManager,
                authenticationSuccessHandler
        );
    }

    //성공 시 작동하는 클래스
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new BootAuthenticationSuccessHandler();
    }

    //패스워크 인코딩
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // css,javascript, 이미지등 정적 리소스 spring security 대상에서 제외
        return (web) ->
                web
                        .ignoring()
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations()
                        );
    }
}
