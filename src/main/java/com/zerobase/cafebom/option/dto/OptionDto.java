package com.zerobase.cafebom.option.dto;

import lombok.Builder;
import lombok.Getter;

public class OptionDto {

    @Getter
    @Builder
    public static class Request {

        private Integer optionCategoryId;

        private String name;

        private Integer price;

        public static OptionDto.Request from(OptionForm.Request form) {
            return OptionDto.Request.builder()
                .optionCategoryId(form.getOptionCategoryId())
                .name(form.getName())
                .price(form.getPrice())
                .build();
        }
    }
}