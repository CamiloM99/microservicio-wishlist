package com.sena.wishlist.controller;

import com.sena.wishlist.dto.GlobalMessageResponseDTO;
import com.sena.wishlist.dto.wishlisthistory.WishlistHistoryResponseDTO;
import com.sena.wishlist.service.WishlistHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wishlist-history")
@RequiredArgsConstructor
public class WishlistHistoryController {

    private final WishlistHistoryService wishlistHistoryService;

    // 1. Consultar el historial de movimientos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<GlobalMessageResponseDTO<List<WishlistHistoryResponseDTO>>> getHistoryByUserId(
            @PathVariable Long userId) {
        GlobalMessageResponseDTO<List<WishlistHistoryResponseDTO>> response = wishlistHistoryService.getHistoryByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
