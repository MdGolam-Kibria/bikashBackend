package com.bikash.bikashBackend.filter;

import com.bikash.bikashBackend.dto.UserPrincipal;
import com.bikash.bikashBackend.util.DataUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private String secretKey = "BkashSecureApi";
    private Long expireHour = Long.valueOf("5");

    public String generateToken(Authentication authentication, HttpServletRequest request) {

        UserPrincipal userPrinciple = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        return Jwts.builder().setId(UUID.randomUUID().toString())
                .claim("phone", userPrinciple.getUsername())
                .claim("clientIp", request.getRemoteAddr())
//                .claim("role", userPrinciple.getAuthorities().stream().map(grantedAuthority -> ))
                .setSubject(String.valueOf(userPrinciple.getId()))
                .setIssuedAt(now).setExpiration(DataUtils.getExpirationTime(expireHour))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    public String getClientIpFromJwtToken(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get("clientIp");
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Boolean isValidateToken(String token, HttpServletRequest request) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (!request.getRemoteAddr().equals(getClientIpFromJwtToken(token))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
