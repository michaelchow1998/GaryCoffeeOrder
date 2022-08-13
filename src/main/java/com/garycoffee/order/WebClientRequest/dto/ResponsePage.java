package com.garycoffee.order.WebClientRequest.dto;

import com.garycoffee.order.model.Order;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ResponsePage {

    List<Order> content;
    Long totalElements;
    Integer totalPages;
    boolean last;
    boolean first;
    Integer currentPage;
    Integer size;
}
