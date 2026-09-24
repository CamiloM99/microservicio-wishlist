package com.sena.wishlist.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponseDeseadosDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer currentStock;
    private boolean inStock;
    private String stockMessage;
    private LocalDateTime createdAt;
}
