package com.inventory.config;

import com.inventory.model.*;
import com.inventory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private InMemoryStorage storage;

    @Override
    public void run(String... args) {
        initProducts();
        initStores();
        initStoreInventories();
        initWarehouseInventories();
        initHolidayConfigs();
    }

    private void initProducts() {
        String[] names = {"可口可乐", "百事可乐", "农夫山泉", "康师傅方便面", "统一老坛酸菜面", 
                          "乐事薯片", "奥利奥饼干", "德芙巧克力", "蒙牛纯牛奶", "伊利酸奶"};
        String[] categories = {"饮料", "饮料", "饮料", "食品", "食品", "零食", "零食", "零食", "乳品", "乳品"};
        int[] cycles = {7, 7, 7, 3, 3, 5, 5, 5, 2, 2};
        int[] safetyStocks = {50, 50, 60, 30, 30, 25, 20, 20, 40, 35};
        
        for (int i = 0; i < names.length; i++) {
            Product p = new Product();
            p.setId("P" + (i + 1));
            p.setName(names[i]);
            p.setCategory(categories[i]);
            p.setPrice(2.5 + i * 0.5);
            p.setReplenishmentCycle(cycles[i]);
            p.setSafetyStock(safetyStocks[i]);
            p.setPriorityFactor(1.0);
            storage.products.put(p.getId(), p);
        }
    }

    private void initStores() {
        String[][] stores = {
            {"S1", "北京朝阳门店", "北京市朝阳区建国路88号"},
            {"S2", "上海浦东店", "上海市浦东新区陆家嘴环路1000号"},
            {"S3", "广州天河店", "广州市天河区天河路385号"}
        };
        
        for (String[] s : stores) {
            Store store = new Store();
            store.setId(s[0]);
            store.setName(s[1]);
            store.setAddress(s[2]);
            storage.stores.put(store.getId(), store);
        }
    }

    private void initStoreInventories() {
        String[] storeIds = {"S1", "S2", "S3"};
        String[] productIds = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
        
        for (String storeId : storeIds) {
            for (String productId : productIds) {
                StoreInventory inv = new StoreInventory();
                inv.setId(UUID.randomUUID().toString());
                inv.setStoreId(storeId);
                inv.setProductId(productId);
                inv.setAvailableStock(30 + (int)(Math.random() * 50));
                inv.setInTransitStock(0);
                inv.setLast7DaysSales(20 + (int)(Math.random() * 30));
                inv.setLast30DaysSales(80 + (int)(Math.random() * 100));
                storage.storeInventories.put(inv.getId(), inv);
            }
        }
    }

    private void initWarehouseInventories() {
        String[] productIds = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
        
        for (String productId : productIds) {
            WarehouseInventory inv = new WarehouseInventory();
            inv.setId(UUID.randomUUID().toString());
            inv.setProductId(productId);
            inv.setAvailableStock(500 + (int)(Math.random() * 500));
            inv.setLockedStock(0);
            storage.warehouseInventories.put(inv.getId(), inv);
        }
    }

    private void initHolidayConfigs() {
        HolidayConfig h1 = new HolidayConfig();
        h1.setId("H1");
        h1.setName("春节");
        h1.setStartDate(LocalDate.of(2026, 2, 10));
        h1.setEndDate(LocalDate.of(2026, 2, 24));
        h1.setFactor(1.5);
        storage.holidayConfigs.put(h1.getId(), h1);

        HolidayConfig h2 = new HolidayConfig();
        h2.setId("H2");
        h2.setName("国庆");
        h2.setStartDate(LocalDate.of(2026, 10, 1));
        h2.setEndDate(LocalDate.of(2026, 10, 7));
        h2.setFactor(1.3);
        storage.holidayConfigs.put(h2.getId(), h2);
    }
}
