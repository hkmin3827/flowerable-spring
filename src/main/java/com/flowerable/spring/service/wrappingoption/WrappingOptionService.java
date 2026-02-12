package com.flowerable.spring.service.wrappingoption;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.dto.wrappingoption.WrappingOptionRes;
import com.flowerable.spring.entity.wrappingoption.WrappingOption;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.repository.WrappingOptionRepository;
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
