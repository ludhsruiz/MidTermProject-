package com.ironhack.midterm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfiguration {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/account-holder/{id}").hasAnyRole("ACCOUNT_HOLDER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/savings/{id}").hasAnyRole("ACCOUNT_HOLDER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/creditcard/{id}").hasAnyRole("ACCOUNT_HOLDER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/checking/{id}").hasAnyRole("ACCOUNT_HOLDER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/third-party/{id}").hasAnyRole("THIRD_PARTY", "ADMIN")
                .antMatchers(HttpMethod.GET, "/transactions/{id}").hasAnyRole("ACCOUNT_HOLDER", "THIRD_PARTY", "ADMIN")
                .antMatchers(HttpMethod.GET, "/transactions/savings/{id}").hasAnyRole("ACCOUNT_HOLDER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/transfer/third-party").hasRole("THIRD_PARTY")
                .antMatchers(HttpMethod.PUT, "/transfer/{accountType}/{value}/{owner}/{id}").hasRole("ACCOUNT_HOLDER")
                .antMatchers(HttpMethod.GET, "/savings").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/third-party").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/account-holder").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/creditcard").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/checking").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/transactions").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/create/account-holder").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/create/savings").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/create/creditcard").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/create/checking").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/create/third-party").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/modify/savings/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/modify/creditcard/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/modify/checking/{id}").hasRole("ADMIN")
                .anyRequest().permitAll();
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
