package com.sena.wishlist.filter;

import com.sena.wishlist.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTValidationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no hay token, dejamos pasar la petición para que las reglas de SecurityConfig decidan
        // (por ejemplo, si en un futuro tienes rutas públicas)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Extraemos quitando "Bearer "

        try {
            if (jwtService.isTokenValid(token)) {
                String email = jwtService.extractSubject(token);
                Long rolId = jwtService.extractRolId(token);
                Long userId = jwtService.extractUserId(token);

                // Mapeamos el rolId numérico al nombre de autoridad que espera SecurityConfig
                // 1 -> USER, 2 -> ADMIN (según tu base de datos)
                String roleName = (rolId != null && rolId == 2) ? "ADMIN" : "USER";

                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(roleName)
                );

                // Creamos el objeto de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // AQUÍ ESTABA LO QUE FALTABA: Le decimos a Spring Security quién es el usuario y su rol
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // También conservamos los atributos en el request por si los necesitas en tus controllers
                request.setAttribute("email", email);
                request.setAttribute("rolId", rolId);
                request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            // Si el token es inválido o expiró, limpiamos el contexto
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}