//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import java.io.IOException;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
//        this.jwtService = jwtService;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String requestURI = request.getRequestURI();
//
//        // Пропускаем проверку токена для /api/auth/**
//        if (requestURI.startsWith("/api/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Извлекаем заголовок Authorization
//        final String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        String email = jwtService.extractEmail(token);
//
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            if (jwtService.isTokenValid(token, userDetails)) {
//                var authToken = jwtService.getAuthentication(userDetails);
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            } else {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
