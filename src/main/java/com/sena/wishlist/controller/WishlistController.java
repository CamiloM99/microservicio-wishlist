package com.sena.wishlist.controller;

import com.sena.wishlist.dto.GlobalMessageResponseDTO;
import com.sena.wishlist.dto.wishlist.WishlistRequestDTO;
import com.sena.wishlist.dto.wishlist.WishlistResponseDTO;
import com.sena.wishlist.dto.wishlist.WishlistResponseDeseadosDTO;
import com.sena.wishlist.dto.wishlisthistory.WishlistUpdateRequestDTO;
import com.sena.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // 1. Listar productos deseados con validación de stock
    @GetMapping("/user/{userId}")
    public ResponseEntity<GlobalMessageResponseDTO<List<WishlistResponseDeseadosDTO>>> getWishlistByUserId(
            @PathVariable Long userId) {
        GlobalMessageResponseDTO<List<WishlistResponseDeseadosDTO>> response = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 2. Agregar un producto a la lista de deseos
    @PostMapping("/user/{userId}")
    public ResponseEntity<GlobalMessageResponseDTO<WishlistResponseDTO>> addProduct(
            @PathVariable Long userId,
            @Valid @RequestBody WishlistRequestDTO requestDTO) {
        GlobalMessageResponseDTO<WishlistResponseDTO> response = wishlistService.addProduct(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 3. Actualizar la cantidad de un producto
    @PutMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<GlobalMessageResponseDTO<WishlistResponseDeseadosDTO>> updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody WishlistUpdateRequestDTO requestDTO) {
        GlobalMessageResponseDTO<WishlistResponseDeseadosDTO> response = wishlistService.updateQuantity(userId, productId, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 4. Eliminar un producto de la lista
    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<GlobalMessageResponseDTO<WishlistResponseDeseadosDTO>> removeProduct(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        GlobalMessageResponseDTO<WishlistResponseDeseadosDTO> response = wishlistService.removeProduct(userId, productId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
