package it.epicode.w6d5.devices_management.security;

import it.epicode.w6d5.devices_management.Models.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityChain {
    @Autowired
    private JwtTools jwtTools;
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(AbstractHttpConfigurer::disable);

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/auth/**").permitAll());
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/devices/**").permitAll());
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/employee/**").permitAll());
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/my-profile/**").permitAll());
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/users-administration/**").hasAuthority(UserRole.ADMIN.name()));
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/**").denyAll());
        return httpSecurity.build();
    }
}
