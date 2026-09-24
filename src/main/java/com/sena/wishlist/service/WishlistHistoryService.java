package com.sena.wishlist.service;


import com.sena.wishlist.dto.GlobalMessageResponseDTO;
import com.sena.wishlist.dto.wishlisthistory.WishlistHistoryResponseDTO;
import com.sena.wishlist.entity.WishlistHistory;
import com.sena.wishlist.enums.WishlistAction;
import com.sena.wishlist.repository.WishlistHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistHistoryService {

    private final WishlistHistoryRepository wishlistHistoryRepository;

    private WishlistHistoryResponseDTO convertirDTO(WishlistHistory history) {
        return WishlistHistoryResponseDTO.builder()
                .id(history.getId())
                .userId(history.getUserId())
                .productId(history.getProductId())
                .quantity(history.getQuantity())
                .action(history.getAction().name())
                .description(history.getAction().getDescription())
                .createdAt(history.getCreatedAt())
                .build();
    }
    // consultar un hitorial de un usuario
    public GlobalMessageResponseDTO<List<WishlistHistoryResponseDTO>> getHistoryByUserId(Long userId) {
        GlobalMessageResponseDTO<List<WishlistHistoryResponseDTO>> responseDTO = new GlobalMessageResponseDTO<>();

        List<WishlistHistory> list = wishlistHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (list.isEmpty()) {
            responseDTO.setMessage("No hay movimientos registrados en el historial");
            return responseDTO;
        }

        List<WishlistHistoryResponseDTO> dtoList = list.stream()
                .map(this::convertirDTO)
                .toList();

        responseDTO.setData(dtoList);
        responseDTO.setMessage("Historial obtenido exitosamente");
        return responseDTO;
    }
    // permite registrar cualquier accion en el historico
    public GlobalMessageResponseDTO<WishlistHistoryResponseDTO> registrarAccion(Long userId, Long productId, Integer quantity, WishlistAction action) {
        GlobalMessageResponseDTO<WishlistHistoryResponseDTO> responseDTO = new GlobalMessageResponseDTO<>();

        WishlistHistory history = WishlistHistory.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .action(action)
                .build();

        wishlistHistoryRepository.save(history);

        responseDTO.setData(convertirDTO(history));
        responseDTO.setMessage("Acción registrada en el historial exitosamente");
        return responseDTO;
    }


}

