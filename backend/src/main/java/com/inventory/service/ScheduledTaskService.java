package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.Store;
import com.inventory.model.StoreInventory;
import com.inventory.model.dto.StockAlert;
import com.inventory.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private ReplenishmentCalculationService calculationService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void generateDailyStockAlerts() {
        logger.info("开始生成每日缺货预警...");
        List<StockAlert> alerts = generateStockAlerts();
        for (StockAlert alert : alerts) {
            logger.info("缺货预警 - 门店:{}, 商品:{}, 当前库存:{}, 缺口:{}",
                    alert.getStoreName(), alert.getProductName(),
                    alert.getCurrentStock(), alert.getShortageQuantity());
        }
        logger.info("缺货预警生成完成，共{}条预警", alerts.size());
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public void generateDailyReplenishmentSuggestions() {
        logger.info("开始生成每日补货建议...");
        calculationService.calculateSuggestions(null);
        logger.info("补货建议生成完成");
    }

    public List<StockAlert> generateStockAlerts() {
        List<StockAlert> alerts = new ArrayList<>();
        for (StoreInventory inventory : storage.storeInventories.values()) {
            Product product = storage.products.get(inventory.getProductId());
            Store store = storage.stores.get(inventory.getStoreId());

            int avgDailySales = inventory.getLast7DaysSales() / 7;
            int daysOfStock = avgDailySales > 0 ? inventory.getAvailableStock() / avgDailySales : 999;
            int safetyStock = product.getSafetyStock();

            if (inventory.getAvailableStock() < safetyStock) {
                StockAlert alert = new StockAlert();
                alert.setStoreId(inventory.getStoreId());
                alert.setStoreName(store.getName());
                alert.setProductId(inventory.getProductId());
                alert.setProductName(product.getName());
                alert.setCurrentStock(inventory.getAvailableStock());
                alert.setSafetyStock(safetyStock);
                alert.setShortageQuantity(safetyStock - inventory.getAvailableStock());

                if (daysOfStock <= 1) {
                    alert.setLevel("CRITICAL");
                } else if (daysOfStock <= 3) {
                    alert.setLevel("WARNING");
                } else {
                    alert.setLevel("INFO");
                }

                alerts.add(alert);
            }
        }
        return alerts;
    }
}
