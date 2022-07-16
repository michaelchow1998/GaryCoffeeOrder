package com.garycoffee.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("product")
public class Product {

    @Id
    private String id;
    @Indexed(unique = true)
    @JsonProperty("product_name")
    private String productName;

    @Indexed(unique = true)
    @JsonProperty("short_name")
    private String shortName;
    private String location;
    private String bean;

    @JsonProperty("image_url")
    private String imageUrl;
    private Integer price;
    private Integer stock;
    private String description;

    public Product(String productName, String shortName, String location, String bean, String imageUrl, Integer price, Integer stock, String description) {
        this.productName = productName;
        this.shortName = shortName;
        this.location = location;
        this.bean = bean;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }
}
