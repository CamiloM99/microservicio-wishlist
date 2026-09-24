package com.sena.wishlist.enums;

import lombok.Getter;

@Getter
public enum WishlistAction {
    AGREGADO("Producto agregado a la lista de deseos"),
    ACTUALIZADO("Cantidad actualizada en la lista"),
    ELIMINADO("Producto removido de la lista de deseos");

    private final String description;

    WishlistAction(String description) {
        this.description = description;
    }
}
