package com.garycoffee.order.WebClientRequest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogProduct {

    @JsonProperty("staff_id")
    private Integer staffId;

    @JsonProperty("product_short_name")
    private String productShortName;

    @JsonProperty("transaction_type")
    private TransactionType transactionType;

    private Integer amount;
}
