package com.garycoffee.order.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestUpdateList {

    @JsonProperty("update_list")
    private List<RequestUpdateProduct> requestUpdateProductList;
}
