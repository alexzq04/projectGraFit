package com.grafit.projectGrafit.components;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class NoCache implements Filter {

    // Lista de prefijos de rutas que no deben ser cacheadas
    private static final List<String> NO_CACHE_PATHS = Arrays.asList(
        "/home",
        "/routines",
        "/exercises",
        "/statistics",
        "/exercise-history",
        "/api/",
        "/profile"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Verificar si la ruta actual debe evitar el caché
        boolean shouldNotCache = NO_CACHE_PATHS.stream()
                .anyMatch(prefix -> path.startsWith(prefix));

        if (shouldNotCache) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setDateHeader("Expires", 0);
        }

        chain.doFilter(request, response);
    }
}
