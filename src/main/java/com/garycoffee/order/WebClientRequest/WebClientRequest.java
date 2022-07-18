package com.garycoffee.order.WebClientRequest;

import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.dto.WebClientRequestAccount;
import com.garycoffee.order.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientRequest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Account checkAccountBalance(String phone){
        String uri = "http://localhost:8070/api/v1/accounts/" + phone;
        Account account = webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Account.class)
                .block();

        return account;
    }



    public void addIntegral(String phone, Integer amount){
        WebClientRequestAccount webClientRequest = new WebClientRequestAccount();
        webClientRequest.setPhone(phone);
        webClientRequest.setAmount(amount);

        webClientBuilder.build()
                .put()
                .uri("http://localhost:8070/api/v1/accounts/addIntegral")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(webClientRequest), WebClientRequestAccount.class)
                .retrieve()
                .bodyToMono(Account.class)
                .block();
    }

    public void reduceBalance(String phone, Integer amount){
        WebClientRequestAccount webClientRequest = new WebClientRequestAccount();
        webClientRequest.setPhone(phone);
        webClientRequest.setAmount(amount);

        webClientBuilder.build()
                .put()
                .uri("http://localhost:8070/api/v1/accounts/reduceBalance")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(webClientRequest), WebClientRequestAccount.class)
                .retrieve()
                .bodyToMono(Account.class)
                .block();
    }

    public void reduceIntegral(String phone, Integer amount){
        WebClientRequestAccount webClientRequest = new WebClientRequestAccount();
        webClientRequest.setPhone(phone);
        webClientRequest.setAmount(amount);

        webClientBuilder.build()
                .put()
                .uri("http://localhost:8070/api/v1/accounts/reduceIntegral")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(webClientRequest), WebClientRequestAccount.class)
                .retrieve()
                .bodyToMono(Account.class)
                .block();
    }
}
