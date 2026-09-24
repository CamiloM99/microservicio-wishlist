package com.sena.wishlist.client;

import com.sena.wishlist.dto.GlobalMessageResponseDTO;
import com.sena.wishlist.dto.wishlist.ProductResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient restClient;

    public ProductClient() {
        // Microservicio de productos en el puerto 8082
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public Optional<ProductResponseDTO> getProductById(Long productId) {
        try {
            GlobalMessageResponseDTO<ProductResponseDTO> response = restClient.get()
                    // Agregamos /api/v1 para que coincida exactamente con ProductController
                    .uri("/api/v1/products/{id}", productId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<GlobalMessageResponseDTO<ProductResponseDTO>>() {});

            if (response != null && response.getData() != null) {
                return Optional.of(response.getData());
            }
            return Optional.empty();

        } catch (Exception e) {
            System.err.println("Error al consultar el producto en products-service: " + e.getMessage());
            return Optional.empty();
        }
    }
}