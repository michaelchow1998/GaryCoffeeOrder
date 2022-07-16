package com.garycoffee.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BuyItem {

    @NotNull
    @JsonProperty("product_short_name")
    private String productShortName;

    @NotNull
    private Integer quantity;
}
