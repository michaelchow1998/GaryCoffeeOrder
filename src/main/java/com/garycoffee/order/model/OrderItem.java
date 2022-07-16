package com.garycoffee.order.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem {

    @JsonProperty("product_short_name")
    private String productShortName;
    private Integer quantity;
    private Integer amount;

}
