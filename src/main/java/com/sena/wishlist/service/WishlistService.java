package com.sena.wishlist.service;


import com.sena.wishlist.client.ProductClient;
import com.sena.wishlist.dto.GlobalMessageResponseDTO;
import com.sena.wishlist.dto.wishlist.ProductResponseDTO;
import com.sena.wishlist.dto.wishlist.WishlistRequestDTO;
import com.sena.wishlist.dto.wishlist.WishlistResponseDTO;
import com.sena.wishlist.dto.wishlist.WishlistResponseDeseadosDTO;
import com.sena.wishlist.dto.wishlisthistory.WishlistUpdateRequestDTO;
import com.sena.wishlist.entity.WishList;
import com.sena.wishlist.enums.WishlistAction;
import com.sena.wishlist.repository.WishlistRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Builder
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistHistoryService historyService;
    private final ProductClient productClient;

    // Método auxiliar de mapeo
    private WishlistResponseDTO addProductDTO(WishList wishlist) {
        return WishlistResponseDTO.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUserId())
                .productId(wishlist.getProductId())
                .quantity(wishlist.getQuantity())
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
    // Agregar un producto a la lista de deseos
    public GlobalMessageResponseDTO<WishlistResponseDTO> addProduct(Long userId, WishlistRequestDTO requestDTO) {
        GlobalMessageResponseDTO<WishlistResponseDTO> responseDTO = new GlobalMessageResponseDTO<>();

        if (wishlistRepository.existsByUserIdAndProductId(userId, requestDTO.getProductId())) {
            responseDTO.setMessage("Este producto ya se encuentra en tu lista de deseos");
            return responseDTO;
        }

        WishList wishlist = WishList.builder()
                .userId(userId)
                .productId(requestDTO.getProductId())
                .quantity(requestDTO.getQuantity() != null && requestDTO.getQuantity() > 0 ? requestDTO.getQuantity() : 1)
                .build();

        wishlistRepository.save(wishlist);

        // Se registra la acción mediante el servicio de histórico
        historyService.registrarAccion(userId, wishlist.getProductId(), wishlist.getQuantity(), WishlistAction.AGREGADO);

        responseDTO.setData(addProductDTO(wishlist));
        responseDTO.setMessage("Producto agregado a la lista de deseos exitosamente");
        return responseDTO;
    }

    // Actualizar la cantidad de un producto
    public GlobalMessageResponseDTO<WishlistResponseDeseadosDTO> updateQuantity(Long userId, Long productId, WishlistUpdateRequestDTO requestDTO) {
        GlobalMessageResponseDTO<WishlistResponseDeseadosDTO> responseDTO = new GlobalMessageResponseDTO<>();

        if (requestDTO.getQuantity() == null || requestDTO.getQuantity() <= 0) {
            responseDTO.setMessage("La cantidad debe ser mayor a 0");
            return responseDTO;
        }

        Optional<WishList> wishlistOptional = wishlistRepository.findByUserIdAndProductId(userId, productId);

        if (wishlistOptional.isEmpty()) {
            responseDTO.setMessage("No se puede actualizar. El producto no existe en tu lista de deseos");
            return responseDTO;
        }

        WishList wishlist = wishlistOptional.get();
        wishlist.setQuantity(requestDTO.getQuantity());

        wishlistRepository.save(wishlist);

        historyService.registrarAccion(userId, productId, wishlist.getQuantity(), WishlistAction.ACTUALIZADO);

        responseDTO.setData(convertirDTO(wishlist));
        responseDTO.setMessage("Cantidad actualizada exitosamente");
        return responseDTO;
    }

    // Eliminar un producto de la lista
    public GlobalMessageResponseDTO<WishlistResponseDeseadosDTO> removeProduct(Long userId, Long productId) {
        GlobalMessageResponseDTO<WishlistResponseDeseadosDTO> responseDTO = new GlobalMessageResponseDTO<>();

        Optional<WishList> wishlistOptional = wishlistRepository.findByUserIdAndProductId(userId, productId);

        if (wishlistOptional.isEmpty()) {
            responseDTO.setMessage("No se puede eliminar. El producto no existe en tu lista de deseos");
            return responseDTO;
        }

        WishList wishlist = wishlistOptional.get();

        // Se registra en el histórico antes de eliminar el registro de la tabla activa
        historyService.registrarAccion(userId, productId, wishlist.getQuantity(), WishlistAction.ELIMINADO);

        wishlistRepository.delete(wishlist);

        responseDTO.setData(convertirDTO(wishlist));
        responseDTO.setMessage("Producto eliminado de la lista de deseos exitosamente");
        return responseDTO;
    }
    // metodo para mostrar la lista de deseados
    public GlobalMessageResponseDTO<List<WishlistResponseDeseadosDTO>> getWishlistByUserId(Long userId) {
        GlobalMessageResponseDTO<List<WishlistResponseDeseadosDTO>> responseDTO = new GlobalMessageResponseDTO<>();

        List<WishList> list = wishlistRepository.findByUserId(userId);

        if (list.isEmpty()) {
            responseDTO.setMessage("La lista de deseos está vacía");
            responseDTO.setData(new ArrayList<>());
            return responseDTO;
        }

        List<WishlistResponseDeseadosDTO> dtoList = new ArrayList<>();
        boolean hayProductosSinStock = false;

        for (WishList item : list) {

            Optional<ProductResponseDTO> productOpt = productClient.getProductById(item.getProductId());

            WishlistResponseDeseadosDTO dto = convertirDTO(item, productOpt.orElse(null));

            if (!dto.isInStock()) {
                hayProductosSinStock = true;
            }

            dtoList.add(dto);
        }

        responseDTO.setData(dtoList);

        if (hayProductosSinStock) {
            responseDTO.setMessage("Lista obtenida. Aviso: Uno o más productos de tu lista de deseos ya no cuentan con existencias.");
        } else {
            responseDTO.setMessage("Lista de deseos obtenida exitosamente");
        }

        return responseDTO;
    }

    private WishlistResponseDeseadosDTO convertirDTO(WishList wishlist) {
        return convertirDTO(wishlist, null);
    }
    // Mapea la información del producto, valida el stock y arma la notificación
    private WishlistResponseDeseadosDTO convertirDTO(WishList item, ProductResponseDTO product) {
        String productName = "Producto no disponible";
        Integer currentStock = 0;
        boolean inStock = false;
        String stockMessage;

        if (product != null) {
            productName = product.getName();
            currentStock = (product.getStockQuantity() != null) ? product.getStockQuantity() : 0;
            inStock = currentStock > 0;
            stockMessage = inStock ? null : "¡Agotado! Este producto ya no cuenta con existencias.";
        } else {
            stockMessage = "Atención: El producto ya no existe en el catálogo.";
        }

        return WishlistResponseDeseadosDTO.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .productId(item.getProductId())
                .productName(productName)
                .quantity(item.getQuantity())
                .currentStock(currentStock)
                .inStock(inStock)
                .stockMessage(stockMessage)
                .createdAt(item.getCreatedAt())
                .build();
    }

}
