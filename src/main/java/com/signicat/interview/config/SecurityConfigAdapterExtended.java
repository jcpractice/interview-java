package com.signicat.interview.config;

import com.signicat.interview.filter.JWTAuthFilter;
import com.signicat.interview.service.UserDetailsServiceExtended;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfigAdapterExtended extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceExtended userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.trace("Entry http configure");

        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/v3/api-docs/**", "/swagger-ui/**","/swagger-resources/configuration/ui",
                "/swagger-resources", "/swagger-resources/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll();

        http.exceptionHandling().authenticationEntryPoint((request, response, e) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{ \"message\": \"Invalid UserName or Password\" }");
        });
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        log.trace("Exit http configure");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.trace("Entry authentication configure");

        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        log.trace("Exit authentication configure");
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Bean
    public JWTAuthFilter jwtAuthenticationFilter() {
        return new JWTAuthFilter();
    }
}
