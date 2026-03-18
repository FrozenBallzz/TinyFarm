package com.tinyfarm.config;

import com.tinyfarm.service.GithubUserProvisioningService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final DevAuthProperties devAuthProperties;

    public SecurityConfig(DevAuthProperties devAuthProperties) {
        this.devAuthProperties = devAuthProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**").permitAll()
                .requestMatchers("/dev-login").permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout.logoutSuccessUrl("/"));

        if (!devAuthProperties.enabled()) {
            http.oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
            );
        }

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
        GithubUserProvisioningService provisioningService
    ) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User user = delegate.loadUser(request);
            provisioningService.provisionUser(user);
            return new DefaultOAuth2User(user.getAuthorities(), user.getAttributes(), "login");
        };
    }
}
