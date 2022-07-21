package com.garycoffee.order.WebClientRequest;

import com.garycoffee.order.WebClientRequest.dto.RequestLogProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductLogWebClientRequest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public String createProductLog(RequestLogProduct req){


        String uri = "https://gary-coffee-log.herokuapp.com/api/v1/product-log";
        String message = webClientBuilder.build()
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(req))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return message;
    }
}
