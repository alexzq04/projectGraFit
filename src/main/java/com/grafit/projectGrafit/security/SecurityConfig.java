package com.grafit.projectGrafit.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import com.grafit.projectGrafit.services.UsuarioDetailsService;

/**
 * Configuración de seguridad para la aplicación utilizando Spring Security.
 * Define las reglas de acceso, la configuración de login/logout y la gestión de usuarios.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    /**
     * Constructor que inyecta el servicio de detalles de usuario personalizado.
     * @param usuarioDetailsService Servicio para cargar usuarios desde la base de datos.
     */
    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define las rutas públicas, privadas, la página de login y logout.
     * 
     * @param http Objeto de configuración de seguridad HTTP.
     * @return SecurityFilterChain configurada.
     * @throws Exception en caso de error de configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/index", "/powerlifting", "/css/**", "/js/**", "/images/**", "/register", "/login").permitAll()
                .requestMatchers("/statistics/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
            );

        return http.build();
    }

    /**
     * Configura el AuthenticationManager con el servicio de usuarios y el codificador de contraseñas.
     * 
     * @param http Objeto de configuración de seguridad HTTP.
     * @return AuthenticationManager configurado.
     * @throws Exception en caso de error de configuración.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(usuarioDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    /**
     * Proporciona el codificador de contraseñas BCrypt para almacenar contraseñas de forma segura.
     * 
     * @return PasswordEncoder basado en BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}