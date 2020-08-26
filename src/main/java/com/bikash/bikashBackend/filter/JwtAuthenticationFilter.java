package com.bikash.bikashBackend.filter;


import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.Service.CustomUserDetailsService;
import com.bikash.bikashBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(httpServletRequest);
           if (StringUtils.hasText(jwt) && jwtTokenProvider.isValidateToken(jwt,httpServletRequest)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                User user = userRepository.findByIdAndIsActiveTrue(userId);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getPhone());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //String ip = request.getRemoteAddr();
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
