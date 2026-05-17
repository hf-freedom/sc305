package com.inventory.service;

import com.inventory.model.SaleRecord;
import com.inventory.model.StoreInventory;
import com.inventory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SaleService {

    @Autowired
    private InMemoryStorage storage;

    public void processSale(String storeId, String productId, Integer quantity) {
        StoreInventory inventory = storage.storeInventories.values().stream()
                .filter(inv -> inv.getStoreId().equals(storeId) && inv.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("库存记录不存在"));

        if (inventory.getAvailableStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        inventory.setAvailableStock(inventory.getAvailableStock() - quantity);
        inventory.setLast7DaysSales(inventory.getLast7DaysSales() + quantity);
        inventory.setLast30DaysSales(inventory.getLast30DaysSales() + quantity);

        SaleRecord record = new SaleRecord();
        record.setId(UUID.randomUUID().toString());
        record.setStoreId(storeId);
        record.setProductId(productId);
        record.setQuantity(quantity);
        record.setSaleTime(LocalDateTime.now());
        storage.saleRecords.put(record.getId(), record);
    }
}
