package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.model.HolidayConfig;
import com.inventory.model.Product;
import com.inventory.model.Store;
import com.inventory.model.dto.StockAlert;
import com.inventory.service.ScheduledTaskService;
import com.inventory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommonController {

    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @GetMapping("/stores")
    public Result<List<Store>> getStores() {
        return Result.success(new ArrayList<>(storage.stores.values()));
    }

    @GetMapping("/products")
    public Result<List<Product>> getProducts() {
        return Result.success(new ArrayList<>(storage.products.values()));
    }

    @GetMapping("/holidays")
    public Result<List<HolidayConfig>> getHolidays() {
        return Result.success(new ArrayList<>(storage.holidayConfigs.values()));
    }

    @GetMapping("/alerts")
    public Result<List<StockAlert>> getAlerts() {
        return Result.success(scheduledTaskService.generateStockAlerts());
    }
}
