package com.flowerable.spring.entity.flower;

import com.flowerable.spring.constant.Season;
import com.flowerable.spring.dto.flower.FlowerCreateReq;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "flowers")
@Getter @NoArgsConstructor
@AllArgsConstructor
public class Flower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String floralLang;   // 꽃말

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 20)
    private Season category;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }


    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public Flower(FlowerCreateReq dto){
        this.name = dto.getName();
        this.floralLang = dto.getFloralLang();
        this.imageUrl = dto.getImageUrl();
        this.category = dto.getCategory();
    }

    public void updateInfo(FlowerCreateReq dto) {
        if(dto.getName() != null) {
            this.name = dto.getName();
        }
        if(dto.getFloralLang() != null){
            this.floralLang = dto.getFloralLang();
        }
        if(dto.getCategory() != null){
            this.category = dto.getCategory();
        }
        if(dto.getImageUrl() != null){
            this.imageUrl = dto.getImageUrl();
        }
    }

}
