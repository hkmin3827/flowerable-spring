package com.flowerable.spring.service.cart;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.shopflower.Color;
import com.flowerable.spring.dto.cart.CartRequest;
import com.flowerable.spring.dto.cart.CartResponse;
import com.flowerable.spring.entity.cart.Cart;
import com.flowerable.spring.entity.cart.CartItem;
import com.flowerable.spring.entity.cart.CartItemDetail;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import com.flowerable.spring.entity.user.User;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.repository.*;
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
    private final CartItemDetailRepository cartItemDetailRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopFlowerRepository shopFlowerRepository;
    
    /**
     * 장바구니 조회 (없으면 생성)
     */
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
    
    /**
     * 장바구니 아이템 개수 조회
     */
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
    
    /**
     * 장바구니에 추가
     */
    @Transactional
    public CartResponse.CartInfo addToCart(Long accountId, CartRequest.AddToCart request) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Shop shop = shopRepository.findByIdAndDeletedAtIsNullAndIsActive(request.getShopId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));
        
        // 장바구니 조회 또는 생성
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(user);
                    return cartRepository.save(newCart);
                });
        
        // 해당 샵의 CartItem이 이미 있는지 확인
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndShopId(cart.getId(), shop.getId());
        
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.updateWrapping(request.getWrappingColorName(), request.getWrappingExtraPrice());
            cartItem.updateMessage(request.getMessage());
        } else {
            cartItem = CartItem.create(
                    shop,
                    request.getWrappingColorName(),
                    request.getWrappingExtraPrice(),
                    request.getMessage()
            );
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
        }
        
        // 꽃 상세 추가
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
    
    /**
     * 장바구니 항목 삭제
     */
    @Transactional
    public CartResponse.CartInfo removeCartItem(Long accountId, Long cartItemId) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));
        
        // 권한 확인
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
        
        return CartResponse.CartInfo.from(cart);
    }
    
//    /**
//     * 장바구니 항목 수정
//     */
//    @Transactional
//    public CartResponse.CartInfo updateCartItem(Long accountId, Long cartItemId, CartRequest.UpdateCartItem request) {
//        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
//
//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));
//
//        // 권한 확인
//        if (!cartItem.getCart().getId().equals(cart.getId())) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED);
//        }
//
//        // 기존 details 삭제
//        cartItemDetailRepository.deleteAll(cartItem.getDetails());
//        cartItem.getDetails().clear();
//
//        // 포장 정보 업데이트
//        cartItem.updateWrapping(request.getWrappingColorName(), request.getWrappingExtraPrice());
//        cartItem.updateMessage(request.getMessage());
//
//        // 새로운 꽃 상세 추가
//        for (CartRequest.UpdateCartItem.FlowerItem flowerItem : request.getFlowers()) {
//            ShopFlower shopFlower = shopFlowerRepository.findById(flowerItem.getShopFlowerId())
//                    .orElseThrow(() -> new CustomException(ErrorCode.SHOP_FLOWER_NOT_FOUND));
//
//            CartItemDetail detail = CartItemDetail.create(
//                    shopFlower,
//                    flowerItem.getQuantity(),
//                    flowerItem.getFlowerColor()
//            );
//            cartItem.addDetail(detail);
//            cartItemDetailRepository.save(detail);
//        }
//
//        return CartResponse.CartInfo.from(cart);
//    }
    
    /**
     * 장바구니 전체 비우기
     */
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
