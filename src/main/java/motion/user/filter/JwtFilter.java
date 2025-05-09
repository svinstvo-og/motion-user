package motion.user.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import motion.user.service.CustomUserDetailsService;
import motion.user.service.JwtService;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean shouldNotFilter = request.getRequestURI().startsWith("/motion/api/v1/user/auth/");
        log.debug("Should not filter: {}", shouldNotFilter);
        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Ayo");

        if (shouldNotFilter(request)) {
            log.debug("Route {} is public", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Authenticating user");
        try {
            String authorization = request.getHeader("Authorization");

            String token = null;
            String username = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                username = jwtService.extractEmail(token);
            }

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class).loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception e) {
            log.error("Authentication error executing route {} : {}", request.getRequestURI(), e.getMessage());
        }
    }
}