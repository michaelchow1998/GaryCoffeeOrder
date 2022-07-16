package com.garycoffee.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("staff_id")
    private Integer staffId;

    @NotEmpty
    @JsonProperty("buy_list")
    private List<BuyItem> buyItemList;
}
