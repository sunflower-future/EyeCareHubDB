package com.example.EyeCareHubDB.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.EyeCareHubDB.dto.AddToCartRequest;
import com.example.EyeCareHubDB.dto.CartDTO;
import com.example.EyeCareHubDB.dto.CartItemDTO;
import com.example.EyeCareHubDB.dto.UpdateCartItemRequest;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.Cart;
import com.example.EyeCareHubDB.entity.CartItem;
import com.example.EyeCareHubDB.entity.ProductVariant;
import com.example.EyeCareHubDB.repository.AccountRepository;
import com.example.EyeCareHubDB.repository.CartItemRepository;
import com.example.EyeCareHubDB.repository.CartRepository;
import com.example.EyeCareHubDB.repository.ProductVariantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AccountRepository accountRepository;

    public CartDTO getMyCart() {
        Cart cart = getOrCreateCart(getUserId());
        return toDTO(cart);
    }

    public CartDTO addItemToCart(AddToCartRequest request) {
        Cart cart = getOrCreateCart(getUserId());
        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        BigDecimal currentPrice = calculateUnitPrice(variant);

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(request.getProductVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock available for total quantity");
            }
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .snapshotPrice(currentPrice)
                    .snapshotProductName(variant.getProduct().getName())
                    .build();
            cart.getCartItems().add(newItem);
        }

        Cart saved = cartRepository.save(cart);
        return toDTO(saved);
    }

    public CartDTO updateItemQuantity(Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(getUserId());
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to this cart");
        }

        if (item.getProductVariant().getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return toDTO(cart);
    }

    public CartDTO removeItemFromCart(Long itemId) {
        Cart cart = getOrCreateCart(getUserId());
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to this cart");
        }

        cart.getCartItems().remove(item);
        cartRepository.save(cart);
        return toDTO(cart);
    }

    public void clearCart() {
        Cart cart = getOrCreateCart(getUserId());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private BigDecimal calculateUnitPrice(ProductVariant variant) {
        BigDecimal basePrice = variant.getProduct().getSalePrice() != null
                ? variant.getProduct().getSalePrice()
                : variant.getProduct().getBasePrice();
        return basePrice.add(variant.getAdditionalPrice() != null ? variant.getAdditionalPrice() : BigDecimal.ZERO);
    }

    private Long getUserId() {
        Long id = com.example.EyeCareHubDB.util.SecurityUtils.getCurrentUserId();
        if (id == null) {
            throw new RuntimeException("You must login first to use this function !!!.");
        }
        return id;
    }

    private Cart getOrCreateCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new RuntimeException("Account not found"));
                    Cart newCart = Cart.builder()
                            .account(account)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private CartDTO toDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        BigDecimal totalPrice = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = itemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        return CartDTO.builder()
                .id(cart.getId())
                .items(itemDTOs)
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .build();
    }

    private CartItemDTO toItemDTO(CartItem item) {
        ProductVariant variant = item.getProductVariant();
        BigDecimal currentPrice = calculateUnitPrice(variant);

        BigDecimal snapshot = item.getSnapshotPrice();
        boolean priceChanged = snapshot != null && snapshot.compareTo(currentPrice) != 0;

        return CartItemDTO.builder()
                .id(item.getId())
                .productVariantId(variant.getId())
                .productName(variant.getProduct().getName())
                .variantSku(variant.getSku())
                .variantColor(variant.getColor())
                .variantSize(variant.getSize())
                .imageUrl(variant.getImageUrl())
                .unitPrice(currentPrice)
                .snapshotPrice(snapshot)
                .priceChanged(priceChanged)
                .quantity(item.getQuantity())
                .subtotal(currentPrice.multiply(new BigDecimal(item.getQuantity())))
                .inStock(variant.getStockQuantity() >= item.getQuantity())
                .availableStock(variant.getStockQuantity())
                .build();
    }
}
