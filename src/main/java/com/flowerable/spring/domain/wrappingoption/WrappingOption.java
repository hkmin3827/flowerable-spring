package com.flowerable.spring.domain.wrappingoption;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="wrapping_options")
public class WrappingOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long shopId;

    @ElementCollection
    @CollectionTable(
        name = "wrapping_option_colors",
        joinColumns = @JoinColumn(name = "wrapping_option_id")
    )
    @Column(name = "color_name", nullable = false)
    private List<String> colorNames = new ArrayList<>();

    @Column(nullable = false)
    private Integer price = 0;

    public void updateOption(List<String> colorNames, Integer price) {
        this.colorNames.clear();
        this.colorNames.addAll(colorNames);

        if(price != null){
            this.price = price;
        }
    }

    public void setShopId(Long shopId){
        this.shopId = shopId;
    }
}
