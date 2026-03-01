package com.flowerable.spring.domain.flower.entity;

import com.flowerable.spring.domain.flower.constant.Season;
import com.flowerable.spring.domain.flower.dto.FlowerCreateReq;
import com.flowerable.spring.domain.flower.dto.FlowerUpdateInfoReq;
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

    @Column(length = 255)
    private String floralLang;   // 꽃말

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Season category;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false, insertable = false)
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

    public void updateInfo(FlowerUpdateInfoReq req) {
        if(req.name() != null) {
            this.name = req.name();;
        }
        if(req.floralLang() != null){
            this.floralLang = req.floralLang();
        }
        if(req.category() != null){
            this.category = req.category();
        }
        if(req.imageUrl() != null){
            this.imageUrl = req.imageUrl();
        }
    }
}
