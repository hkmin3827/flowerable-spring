package com.flowerable.spring.domain.wrappingoption.service;

import com.flowerable.spring.domain.wrappingoption.dto.WrappingOptionRes;
import com.flowerable.spring.domain.wrappingoption.entity.WrappingOption;
import com.flowerable.spring.domain.shop.entity.Shop;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.domain.shop.repository.ShopRepository;
import com.flowerable.spring.domain.wrappingoption.repository.WrappingOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WrappingOptionService {
    private final WrappingOptionRepository wrappingOptionRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public void saveWrappingOptions(Long accountId, List<String> colorNames, Integer price) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        WrappingOption option = wrappingOptionRepository
                .findByShopId(shop.getId())
                .orElseGet(() -> {
                    WrappingOption w = new WrappingOption();
                    w.setShopId(shop.getId());
                    return w;
                });

        option.updateOption(colorNames, price);
        wrappingOptionRepository.save(option);
    }

    @Transactional(readOnly = true)
    public WrappingOptionRes getShopWrappingOption(Long shopId) {
        return wrappingOptionRepository.findByShopId(shopId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public WrappingOptionRes getMyWrappingOption(Long accountId) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return wrappingOptionRepository.findByShopId(shop.getId())
                .map(this::toDto)
                .orElse(null);
    }

    private WrappingOptionRes toDto(WrappingOption option) {
        return WrappingOptionRes.builder()
                .shopId(option.getShopId())
                .colorNames(option.getColorNames())
                .price(option.getPrice())
                .build();
    }
}
