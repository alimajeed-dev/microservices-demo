package com.example.orderservice.service;

import com.example.orderservice.config.WebClientConfig;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineRequestDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService  {
    private final OrderRepo orderRepo;
    private final WebClient.Builder webClientBuilder;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems>orderLineItems = orderRequest.getOrderLineRequestDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuList = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuList).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        if(allProductsInStock) {
            orderRepo.save(order);
        }
        else {
            throw new IllegalArgumentException("Prodcut is not in Inventory");
        }
    }

    private OrderLineItems mapToDto(OrderLineRequestDto orderLineRequestDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineRequestDto.getPrice());
        orderLineItems.setQuantity(orderLineRequestDto.getQuantity());
        orderLineItems.setSkuCode(orderLineRequestDto.getSkuCode());
        return orderLineItems;
    }

}
