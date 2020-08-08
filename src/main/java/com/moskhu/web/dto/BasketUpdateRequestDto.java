package com.moskhu.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasketUpdateRequestDto {
    private String consumerId;
    private Long menuId;

    @Builder
    public BasketUpdateRequestDto(String consumerId, Long menuId){
        this.consumerId = consumerId;
        this.menuId = menuId;
    }
}
