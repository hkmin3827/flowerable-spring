package com.flowerable.spring.application.cart;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.application.cart.dto.CartRequest;
import com.flowerable.spring.application.cart.dto.CartResponse;
import com.flowerable.spring.domain.cart.entity.Cart;
import com.flowerable.spring.domain.cart.entity.CartItem;
import com.flowerable.spring.domain.cart.repository.CartItemRepository;
import com.flowerable.spring.domain.cart.repository.CartRepository;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.domain.shop.ShopRepository;
import com.flowerable.spring.domain.shopflower.ShopFlower;
import com.flowerable.spring.domain.shopflower.ShopFlowerRepository;
import com.flowerable.spring.domain.user.User;
import com.flowerable.spring.domain.user.UserRepository;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopFlowerRepository shopFlowerRepository;
    
    @Transactional
    public CartResponse.CartInfo getCart(Long accountId) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(user);
                    return cartRepository.save(newCart);
                });
        
        return CartResponse.CartInfo.from(cart);
    }

    public CartResponse.CartCount getCartCount(Long accountId) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElse(null);
        
        if (cart == null) {
            return CartResponse.CartCount.of(0);
        }
        
        return CartResponse.CartCount.of(cart.getTotalItemCount());
    }

    @Transactional
    public CartResponse.CartInfo addToCart(Long accountId, CartRequest.AddToCart request) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Shop shop = shopRepository.findByIdAndDeletedAtIsNullAndIsActive(request.getShopId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(user);
                    return cartRepository.save(newCart);
                });
        
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndShopId(cart.getId(), shop.getId());
        
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
        } else {
            cartItem = CartItem.create(
                    shop
            );
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
        }
        
        for (CartRequest.AddToCart.FlowerItem flowerItem : request.getFlowers()) {
            ShopFlower shopFlower = shopFlowerRepository.findById(flowerItem.getShopFlowerId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SHOP_FLOWER_NOT_FOUND));

            cartItem.addFlower(
                    shopFlower,
                    flowerItem.getQuantity(),
                    flowerItem.getFlowerColor()
            );
        }
        
        return CartResponse.CartInfo.from(cart);
    }
    
    @Transactional
    public CartResponse.CartInfo removeCartItem(Long accountId, Long cartItemId) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));
        
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
        
        return CartResponse.CartInfo.from(cart);
    }

    @Transactional
    public void clearCart(Long accountId) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
        
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
    }
}
