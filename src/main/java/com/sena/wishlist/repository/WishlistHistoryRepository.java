package com.sena.wishlist.repository;

import com.sena.wishlist.entity.WishlistHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistHistoryRepository extends JpaRepository<WishlistHistory, Long> {

    // Traer el historial de un usuario ordenado del más reciente al más antiguo
    List<WishlistHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

}