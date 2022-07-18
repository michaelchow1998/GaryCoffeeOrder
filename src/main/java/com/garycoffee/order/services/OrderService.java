package com.garycoffee.order.services;

import com.garycoffee.order.dto.BuyItem;
import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.dto.WebClientRequestAccount;
import com.garycoffee.order.model.Account;
import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.OrderItem;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.OrderRepo;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        int totalAmount = 0;
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();

        //set up orderItemList
        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productRepo.findProductByShortName(buyItem.getProductShortName());

            if(product == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }else if(product.getStock() < buyItem.getQuantity()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            product.setStock(product.getStock() - buyItem.getQuantity());
            productRepo.save(product);

            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            OrderItem orderItem = new OrderItem();
            orderItem.setProductShortName(buyItem.getProductShortName());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);
            orderItemList.add(orderItem);
        }
        if(createOrderRequest.getIsUserBuy().equals(true)){
            //Get Account Balance
            String uri = "http://localhost:8070/api/v1/accounts/" +createOrderRequest.getPhone();
            Account account = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Account.class)
                    .block();

            //Check Account Balance
            assert account != null;
            if(account.getAccountBalance() > totalAmount){

                WebClientRequestAccount webClientRequestAccount = new WebClientRequestAccount();
                webClientRequestAccount.setPhone(createOrderRequest.getPhone());

                //Use Integral
                if(createOrderRequest.getIsUseIntegral().equals(true)){

                    //Total Amount bigger than Integral Balance / 50
                    if(totalAmount > account.getIntegralBalance()/50){

                        totalAmount = totalAmount - account.getIntegralBalance()/50;

                        WebClientRequestAccount webClientRequestIntegral = new WebClientRequestAccount();
                        webClientRequestIntegral.setPhone(createOrderRequest.getPhone());
                        webClientRequestIntegral.setAmount(0);


                        //Reduce Integral Balance money
                        webClientBuilder.build()
                                .put()
                                .uri("http://localhost:8070/api/v1/accounts/reduceIntegral")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(Mono.just(webClientRequestIntegral), WebClientRequestAccount.class)
                                .retrieve()
                                .bodyToMono(Account.class)
                                .block();

                        //Set Reduce Balance number
                        webClientRequestAccount.setAmount(totalAmount);
                    }else{
                        //Total Amount less than Integral Balance /20
                        Integer leaveIntegral = (account.getIntegralBalance()/50 - totalAmount) * 50;

                        totalAmount = 0;

                        WebClientRequestAccount webClientRequestIntegral = new WebClientRequestAccount();
                        webClientRequestIntegral.setPhone(createOrderRequest.getPhone());
                        webClientRequestIntegral.setAmount(leaveIntegral);


                        //Reduce Integral Balance money
                        webClientBuilder.build()
                                .put()
                                .uri("http://localhost:8070/api/v1/accounts/reduceIntegral")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(Mono.just(webClientRequestIntegral), WebClientRequestAccount.class)
                                .retrieve()
                                .bodyToMono(Account.class)
                                .block();

                        //Set Reduce Balance number
                        webClientRequestAccount.setAmount(totalAmount);
                    }
                }else{
                    //Set Reduce Balance number
                    webClientRequestAccount.setAmount(totalAmount);
                }


                //Reduce Balance money
                webClientBuilder.build()
                        .put()
                        .uri("http://localhost:8070/api/v1/accounts/reduceBalance")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(webClientRequestAccount), WebClientRequestAccount.class)
                        .retrieve()
                        .bodyToMono(Account.class)
                        .block();

                //Add Integral Balance
                webClientBuilder.build()
                        .put()
                        .uri("http://localhost:8070/api/v1/accounts/addIntegral")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(webClientRequestAccount), WebClientRequestAccount.class)
                        .retrieve()
                        .bodyToMono(Account.class)
                        .block();


                //set up order
                order.setPhone(createOrderRequest.getPhone());
                order.setStaffId(createOrderRequest.getStaffId());
                order.setTotalAmount(totalAmount);
                order.setIsUserBuy(createOrderRequest.getIsUserBuy());
                order.setIsUseIntegral(createOrderRequest.getIsUseIntegral());
                order.setCreateDate(new Date());
                order.setOrderItemList(orderItemList);
            }else {
                log.warn("not enough money");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }else{
            //set up order
            order.setPhone(createOrderRequest.getPhone());
            order.setStaffId(createOrderRequest.getStaffId());
            order.setTotalAmount(totalAmount);
            order.setIsUserBuy(createOrderRequest.getIsUserBuy());
            order.setIsUseIntegral(createOrderRequest.getIsUseIntegral());
            order.setCreateDate(new Date());
            order.setOrderItemList(orderItemList);
        }
        log.info("save order");
        return orderRepo.insert(order);

    }

    public List<Order> getAllOrder(){
        return orderRepo.findAll();
    }

    public Page<Order> getAllOrderWithPage(String phone,Integer page) {
        int size = 50;
        PageRequest pageable = PageRequest.of(page, size);

        //pageResult
        return orderRepo.getOrdersByPhone(phone,pageable);
    }

    public Page<Order> getOrderByStaffId(Integer staffId,Integer page){
        int size = 50;
        PageRequest pageable = PageRequest.of(page, size);

        //pageResult
        return  orderRepo.getOrdersByStaffId(staffId,pageable);
    }
    public Order getOrderById(String Id){
        return orderRepo.getOrderById(Id);
    }

}
