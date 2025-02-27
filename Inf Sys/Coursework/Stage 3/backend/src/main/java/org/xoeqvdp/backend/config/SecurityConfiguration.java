//package org.xoeqvdp.backend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.xoeqvdp.backend.filters.JwtAuthFilter;
//
//@Configuration
//public class SecurityConfiguration {
//
//    private final JwtAuthFilter jwtAuthFilter;
//
//    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter) {
//        this.jwtAuthFilter = jwtAuthFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthFilter);
//
//        return http.build();
//    }
//}
