package com.sena.wishlist.dto.wishlisthistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistHistoryResponseDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String action;
    private String description;
    private LocalDateTime createdAt;
}
