package com.flowerable.spring.service.shopimage;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.dto.shopimage.ShopImageCreateReq;
import com.flowerable.spring.dto.shopimage.ShopImageRes;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.entity.shop.ShopImage;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.repository.ShopImageRepository;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.flowerable.spring.constant.common.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopImageService {

    private final ShopRepository shopRepository;
    private final ShopImageRepository shopImageRepository;

    @Transactional
    public void uploadImages(Long accountId, ShopImageCreateReq req) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        for (String url : req.imageUrls()) {
            shopImageRepository.save(
                    ShopImage.create(
                            shop,
                            url
                    )
            );
        }
    }

    @Transactional
    public void deleteImage(Long accountId, Long imageId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(ShopNotFoundException::new);

        ShopImage image = shopImageRepository.findByIdAndShopId(imageId, shopId)
                .orElseThrow(() -> new CustomException(IMAGE_NOT_FOUND));

        shopImageRepository.delete(image);
    }

    // 전체 이미지 조회 (8개씩, 무한 스크롤)
    @Transactional(readOnly = true)
    public List<ShopImageRes> getShopImages(Long shopId, Long lastId) {

        List<ShopImage> images;

        if (lastId == null) {
            images = shopImageRepository
                    .findTop8ByShopIdOrderByIdDesc(shopId);
        } else {
            images = shopImageRepository
                    .findTop8ByShopIdAndIdLessThanOrderByIdDesc(
                            shopId,
                            lastId
                    );
        }
        return images.stream()
                .map(ShopImageRes::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShopImageRes> getMyShopImages(
            Long accountId,
            Long lastId
    ) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(ShopNotFoundException::new);

        List<ShopImage> images;

        if (lastId == null) {
            images = shopImageRepository
                    .findTop8ByShopIdOrderByIdDesc(shopId);
        } else {
            images = shopImageRepository
                    .findTop8ByShopIdAndIdLessThanOrderByIdDesc(
                            shopId,
                            lastId
                    );
        }

        return images.stream()
                .map(ShopImageRes::from)
                .toList();
    }

    
    // 샵 상세 (최신 이미지 5개)
    @Transactional(readOnly = true)
    public List<ShopImageRes> getLatestImages(Long shopId) {
        return shopImageRepository.findTop5ByShopIdOrderByCreatedAtDesc(shopId)
                .stream()
                .map(ShopImageRes::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShopImageRes> getMyLatestImages(Long accountId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return shopImageRepository.findTop5ByShopIdOrderByCreatedAtDesc(shopId)
                .stream()
                .map(ShopImageRes::from)
                .toList();
    }

    /**
     * 대표 이미지 1개 (샵 목록)
     */
    @Transactional(readOnly = true)
    public ShopImageRes getThumbnail(Long shopId) {
        return shopImageRepository
                .findByShopIdAndIsThumbnailTrue(shopId)
                .map(ShopImageRes::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public ShopImageRes getMyThumbnail(Long accountId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return shopImageRepository
                .findByShopIdAndIsThumbnailTrue(shopId)
                .map(ShopImageRes::from)
                .orElse(null);
    }

    @Transactional
    public void registerThumbnail(Long accountId, Long shopImageId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(ShopNotFoundException::new);

        shopImageRepository.clearAllThumbnails(shopId);

        ShopImage shopImage = shopImageRepository.findByIdAndShopId(shopImageId, shopId)
                .orElseThrow(() -> new CustomException(IMAGE_NOT_FOUND));

        shopImage.registerThumbnail();
    }

    @Transactional
    public void clearThumbnail(Long accountId, Long shopImageId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(ShopNotFoundException::new);

        ShopImage shopImage = shopImageRepository.findByIdAndShopId(shopImageId, shopId)
                .orElseThrow(() -> new CustomException(IMAGE_NOT_FOUND));

        shopImage.clearThumbnail();
    }
}