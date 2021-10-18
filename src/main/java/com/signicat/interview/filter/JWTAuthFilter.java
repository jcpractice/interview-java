package com.signicat.interview.filter;

import com.nimbusds.jose.JOSEException;
import com.signicat.interview.exception.CustomTokenException;
import com.signicat.interview.security.util.TokenFactory;
import com.signicat.interview.service.UserDetailsServiceExtended;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    UserDetailsServiceExtended userDetailsServiceExtended;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.trace("Inside filter :::");
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
       log.trace("requestTokenHeader--"+requestTokenHeader);
        if(requestTokenHeader != null){
            if(requestTokenHeader.startsWith("Bearer ")){
                jwtToken = requestTokenHeader.substring(7);
                try {
                    username = tokenFactory.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException | ParseException e) {
                   log.error("Exception occurred while parsing provided token.."+e.getLocalizedMessage());

                   throw new RuntimeException("Invalid Token provided.",e);
                }
            }else{
                logger.warn("JWT Token does not begin with Bearer String");
                resolver.resolveException(request, response, null, new CustomTokenException("Invalid Token"));
            }

        }else{
            //proceed as anonymous
            log.info("User will be proceeded as anonymous..");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsServiceExtended.loadUserByUsername(username);
            try {
                if (tokenFactory.validateToken(jwtToken, userDetails)) {
                   log.trace("valid token..");
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }else{
                    log.trace("in-valid token..");
                    resolver.resolveException(request, response, null, new CustomTokenException("Invalid Token"));
                }
            } catch (JOSEException | ParseException e) {
                logger.error("Exception Occurred : "+e.getLocalizedMessage());
                resolver.resolveException(request, response, null, new CustomTokenException("Unable to parse token"));
            }
        }
        filterChain.doFilter(request, response);
    }
}
