package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.model.StoreInventory;
import com.inventory.model.WarehouseInventory;
import com.inventory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InMemoryStorage storage;

    @GetMapping("/store/{storeId}")
    public Result<List<StoreInventory>> getStoreInventory(@PathVariable String storeId) {
        List<StoreInventory> list = storage.storeInventories.values().stream()
                .filter(inv -> inv.getStoreId().equals(storeId))
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @GetMapping("/warehouse")
    public Result<List<WarehouseInventory>> getWarehouseInventory() {
        return Result.success(new ArrayList<>(storage.warehouseInventories.values()));
    }
}
