package com.sena.wishlist.repository;

import com.sena.wishlist.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishList, Long> {

    // Obtener todos los productos de la lista de un usuario concreto
    List<WishList> findByUserId(Long userId);

    // Verificar si el usuario ya tiene ese producto agregado antes de duplicarlo
    Optional<WishList> findByUserIdAndProductId(Long userId, Long productId);

    // Comprobar existencia rápidamente sin cargar la entidad
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Eliminar un producto específico de la lista de un usuario
    void deleteByUserIdAndProductId(Long userId, Long productId);
}