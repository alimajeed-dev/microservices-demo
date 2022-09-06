package com.example.orderservice.service;

import com.example.orderservice.dto.OrderLineRequestDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService  {
    private final OrderRepo orderRepo;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems>orderLineItems = orderRequest.getOrderLineRequestDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        orderRepo.save(order);
    }

    private OrderLineItems mapToDto(OrderLineRequestDto orderLineRequestDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineRequestDto.getPrice());
        orderLineItems.setQuantity(orderLineRequestDto.getQuantity());
        orderLineItems.setSkuCode(orderLineRequestDto.getSkuCode());
        return orderLineItems;
    }

}
