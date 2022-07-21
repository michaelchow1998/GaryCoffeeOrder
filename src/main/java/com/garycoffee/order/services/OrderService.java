package com.garycoffee.order.services;

import com.garycoffee.order.WebClientRequest.UserLogWebClientRequest;
import com.garycoffee.order.WebClientRequest.WebClientRequest;
import com.garycoffee.order.WebClientRequest.dto.RequestLogUser;
import com.garycoffee.order.WebClientRequest.dto.TransactionType;
import com.garycoffee.order.dto.BuyItem;
import com.garycoffee.order.dto.CreateOrderRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

    @Autowired
    private WebClientRequest webClientRequest;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserLogWebClientRequest userLogWebClientRequest;

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

        //set originAmount
        order.setOriginAmount(totalAmount);

        //Reduce Balance number when User
        if(createOrderRequest.getIsUserBuy().equals(true)){

            //Get Account Balance
           Account account =webClientRequest
                   .checkAccountBalance(createOrderRequest.getPhone());

            //Check Account Balance
            assert account != null;
            if(account.getAccountBalance() > totalAmount){

                //Is Use Integral
                if(createOrderRequest.getIsUseIntegral().equals(true)){

                    //Total Amount bigger than Integral Balance / 50
                    if(totalAmount > account.getIntegralBalance()/50){

                        totalAmount = totalAmount - account.getIntegralBalance()/50;

                        //Reduce Integral number
                        webClientRequest.reduceIntegral(createOrderRequest.getPhone(),account.getIntegralBalance());

                        //Reduce Balance number
                        webClientRequest.reduceBalance(createOrderRequest.getPhone(),totalAmount);

                        userLogWebClientRequest.createUserLog(
                                new RequestLogUser(
                                        createOrderRequest.getPhone(),
                                        TransactionType.Reduce,
                                        createOrderRequest.getPhone() + " Account Balance: reduce " + totalAmount + " $"
                                        ));

                        order.setTotalAmount(totalAmount);
                    }else{
                        //Total Amount less than Integral Balance /20
                        Integer leaveIntegral = (account.getIntegralBalance()/50 - totalAmount) * 50;

                        totalAmount = 0;

                        //Reduce Integral number
                        webClientRequest.reduceIntegral(createOrderRequest.getPhone(),leaveIntegral);

                        //Reduce Balance number
                        webClientRequest.reduceBalance(createOrderRequest.getPhone(),totalAmount);

                        userLogWebClientRequest.createUserLog(
                                new RequestLogUser(
                                        createOrderRequest.getPhone(),
                                        TransactionType.Reduce,
                                        createOrderRequest.getPhone() + " Account Balance: reduce " + totalAmount + " $"
                                ));

                        order.setTotalAmount(totalAmount);
                    }
                }
                //Not Use Integral
                else{
                    //Reduce Balance number when not User
                    webClientRequest.reduceBalance(createOrderRequest.getPhone(),totalAmount);
                    order.setTotalAmount(totalAmount);
                }

                //Add Integral Balance
                webClientRequest.addIntegral(createOrderRequest.getPhone(),
                        order.getOriginAmount()
                );

            }else {
                log.warn("not enough money");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        //set up order
        order.setPhone(createOrderRequest.getPhone());
        order.setStaffId(createOrderRequest.getStaffId());
        order.setIsUserBuy(createOrderRequest.getIsUserBuy());
        order.setIsUseIntegral(createOrderRequest.getIsUseIntegral());
        order.setCreateDate(new Date());
        order.setOrderItemList(orderItemList);

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
