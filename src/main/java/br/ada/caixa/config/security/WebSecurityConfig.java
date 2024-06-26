//package br.ada.caixa.config.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
////@Configuration
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(
////        prePostEnabled = true,
////        securedEnabled = true,
////        jsr250Enabled = true)
//public class WebSecurityConfig {
//
//    final UserServiceImpl userDetailsService;
//
//    public WebSecurityConfig(UserServiceImpl userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//
//        http.cors().and().csrf().disable()
//                .authorizeRequests()
//                    .antMatchers("/swagger-ui/**").permitAll()
//                    .antMatchers("/swagger-ui.html").permitAll()
//                    .antMatchers("/v3/**").permitAll()
//                    .antMatchers("/h2-console/**").permitAll()
//                    .anyRequest().authenticated()
//                .and()
//                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // habilita os frames do h2-console
//                .addFilter(new JWTAuthenticationFilter(authenticationManager)) //autenticacao
//                .addFilter(new JWTAuthorizationFilter(authenticationManager)) //autorizacao
//                .authenticationManager(authenticationManager)
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        return http.build();
//    }
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**").allowedMethods("*");
//            }
//        };
//    }
//
//}
