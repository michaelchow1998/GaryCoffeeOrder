package com.garycoffee.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document("order")
public class Order {

    @Id
    private String id;

    private String phone;

    @JsonProperty("staff_id")
    private Integer staffId;

    private Integer originAmount;

    private Integer totalAmount;

    @JsonProperty("is_user_buy")
    private Boolean isUserBuy;

    @JsonProperty("is_use_integral")
    private Boolean isUseIntegral;


    private Date createDate;

    @JsonProperty("item_list")
    private List<OrderItem> orderItemList;
}
