package com.garycoffee.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    private String phone;

    @JsonProperty("staff_id")
    private Integer staffId;

    @JsonProperty("is_user_buy")
    private Boolean isUserBuy;

    @JsonProperty("is_use_integral")
    private Boolean isUseIntegral;

    @NotEmpty
    @JsonProperty("buy_list")
    private List<BuyItem> buyItemList;
}
