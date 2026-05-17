package com.inventory.storage;

import com.inventory.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {
    public final Map<String, Product> products = new ConcurrentHashMap<>();
    public final Map<String, Store> stores = new ConcurrentHashMap<>();
    public final Map<String, StoreInventory> storeInventories = new ConcurrentHashMap<>();
    public final Map<String, WarehouseInventory> warehouseInventories = new ConcurrentHashMap<>();
    public final Map<String, ReplenishmentRequest> replenishmentRequests = new ConcurrentHashMap<>();
    public final Map<String, SaleRecord> saleRecords = new ConcurrentHashMap<>();
    public final Map<String, HolidayConfig> holidayConfigs = new ConcurrentHashMap<>();
}
