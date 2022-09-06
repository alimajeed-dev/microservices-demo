package com.example.inventoryservice.service;

import com.example.inventoryservice.repo.InventoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepo inventoryRepo;
    public boolean isInStock(String skuCode){
        return inventoryRepo.findBySkuCode(skuCode).isPresent();
    }

}
