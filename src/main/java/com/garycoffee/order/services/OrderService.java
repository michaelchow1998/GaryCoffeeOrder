package com.garycoffee.order.services;

import com.garycoffee.order.WebClientRequest.ProductLogWebClientRequest;
import com.garycoffee.order.WebClientRequest.WebClientRequest;
import com.garycoffee.order.WebClientRequest.dto.RequestLogProduct;
import com.garycoffee.order.WebClientRequest.dto.ResponsePage;
import com.garycoffee.order.WebClientRequest.dto.TransactionType;
import com.garycoffee.order.dto.BuyItem;
import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.exception.BalanceNotEnoughException;
import com.garycoffee.order.exception.NotFoundException;
import com.garycoffee.order.exception.StockNotEnoughException;
import com.garycoffee.order.model.Account;
import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.OrderItem;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.OrderRepo;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

    @Resource
    private WebClientRequest webClientRequest;

    @Resource
    private OrderRepo orderRepo;

    @Resource
    private ProductRepo productRepo;


    @Resource
    private ProductLogWebClientRequest productLogWebClientRequest;

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        int totalAmount = 0;
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();

        //set up orderItemList
        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productRepo.findProductByShortName(buyItem.getProductShortName());

            if(product == null){
                throw new NotFoundException("Can't find the product");
            }else if(product.getStock() < buyItem.getQuantity()){
                throw new StockNotEnoughException(product.getProductName()+": have not enough to stock to sell.");
            }

            product.setStock(product.getStock() - buyItem.getQuantity());
            productRepo.save(product);

            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            //Product log
            RequestLogProduct logReq = new RequestLogProduct();
            logReq.setStaffId(createOrderRequest.getStaffId());
            logReq.setProductShortName(buyItem.getProductShortName());
            logReq.setTransactionType(TransactionType.Reduce);
            logReq.setAmount(buyItem.getQuantity());

            String logMessage = productLogWebClientRequest.createProductLog(logReq);
            log.info(logMessage);

            //set OrderItem
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


                        order.setTotalAmount(totalAmount);
                    }else{
                        //Total Amount less than Integral Balance /20
                        Integer leaveIntegral = (account.getIntegralBalance()/50 - totalAmount) * 50;

                        totalAmount = 0;

                        //Reduce Integral number
                        webClientRequest.reduceIntegral(createOrderRequest.getPhone(),leaveIntegral);

                        //Reduce Balance number
                        webClientRequest.reduceBalance(createOrderRequest.getPhone(),totalAmount);


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
                throw new BalanceNotEnoughException(
                        createOrderRequest.getPhone() +": have not enough money to buy those products");
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

    public ResponsePage getAllOrderWithPage(String phone,Integer page) {
        int size = 50;
        PageRequest pageable = PageRequest.of(page, size);

        //pageResult
        return pageToResponsePage(orderRepo.getOrdersByPhone(phone,pageable));
    }

    public ResponsePage getOrderByStaffId(Integer staffId,Integer page){
        int size = 50;
        PageRequest pageable = PageRequest.of(page, size);

        //pageResult
        return pageToResponsePage(orderRepo.getOrdersByStaffId(staffId,pageable));
    }
    public Order getOrderById(String Id){
        return orderRepo.getOrderById(Id);
    }


    public ResponsePage pageToResponsePage(Page<Order> orderPage){
        ResponsePage responsePage = new ResponsePage();
        responsePage.setContent(orderPage.getContent());
        responsePage.setTotalElements(orderPage.getTotalElements());
        responsePage.setTotalPages(orderPage.getTotalPages());
        responsePage.setLast(orderPage.isLast());
        responsePage.setFirst(orderPage.isFirst());
        responsePage.setCurrentPage(orderPage.getNumber());
        responsePage.setSize(orderPage.getSize());
        return responsePage;
    }
}
