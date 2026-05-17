package com.inventory.service;

import com.inventory.model.HolidayConfig;
import com.inventory.model.Product;
import com.inventory.model.Store;
import com.inventory.model.StoreInventory;
import com.inventory.model.dto.ReplenishmentSuggestion;
import com.inventory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplenishmentCalculationService {

    @Autowired
    private InMemoryStorage storage;

    public List<ReplenishmentSuggestion> calculateSuggestions(String storeId) {
        List<ReplenishmentSuggestion> suggestions = new ArrayList<>();
        List<StoreInventory> inventories = storage.storeInventories.values().stream()
                .filter(inv -> storeId == null || inv.getStoreId().equals(storeId))
                .collect(Collectors.toList());

        for (StoreInventory inventory : inventories) {
            Product product = storage.products.get(inventory.getProductId());
            Store store = storage.stores.get(inventory.getStoreId());

            int avgDailySales = inventory.getLast7DaysSales() / 7;
            double holidayFactor = getHolidayFactor();
            int demand = (int) (avgDailySales * product.getReplenishmentCycle() * holidayFactor);
            int totalRequired = demand + product.getSafetyStock();
            int currentTotal = inventory.getAvailableStock() + inventory.getInTransitStock();
            int suggestedQuantity = Math.max(0, totalRequired - currentTotal);

            double salesRate = (double) inventory.getLast30DaysSales() / 30;
            if (salesRate < 2) {
                product.setPriorityFactor(Math.max(0.3, product.getPriorityFactor() - 0.1));
            }

            double priority = suggestedQuantity * product.getPriorityFactor() * holidayFactor;

            if (suggestedQuantity > 0) {
                ReplenishmentSuggestion suggestion = new ReplenishmentSuggestion();
                suggestion.setStoreId(inventory.getStoreId());
                suggestion.setStoreName(store.getName());
                suggestion.setProductId(inventory.getProductId());
                suggestion.setProductName(product.getName());
                suggestion.setSuggestedQuantity(suggestedQuantity);
                suggestion.setCurrentStock(inventory.getAvailableStock());
                suggestion.setAvgDailySales(avgDailySales);
                suggestion.setPriority(priority);
                suggestion.setPriorityFactor(product.getPriorityFactor());
                suggestion.setAvg30DaysSales(salesRate);
                suggestion.setHolidayFactor(holidayFactor);
                suggestion.setIsSlowMoving(salesRate < 2);
                
                String priorityReasonText;
                if (salesRate < 2) {
                    priorityReasonText = String.format("滞销商品(日均%.1f件<2件)，优先级系数降至%.1f", 
                            salesRate, product.getPriorityFactor());
                } else if (product.getPriorityFactor() < 1.0) {
                    priorityReasonText = String.format("历史滞销，优先级系数%.1f", product.getPriorityFactor());
                } else if (holidayFactor > 1.0) {
                    priorityReasonText = String.format("节假日加成%.1f倍", holidayFactor);
                } else {
                    priorityReasonText = "正常优先级";
                }
                suggestion.setPriorityReason(priorityReasonText);
                
                suggestion.setReason(String.format("日均销量%d, 补货周期%d天, 安全库存%d",
                        avgDailySales, product.getReplenishmentCycle(), product.getSafetyStock()));
                suggestions.add(suggestion);
            }
        }

        return suggestions.stream()
                .sorted(Comparator.comparingDouble(ReplenishmentSuggestion::getPriority).reversed())
                .collect(Collectors.toList());
    }

    private double getHolidayFactor() {
        LocalDate today = LocalDate.now();
        return storage.holidayConfigs.values().stream()
                .filter(h -> !today.isBefore(h.getStartDate()) && !today.isAfter(h.getEndDate()))
                .findFirst()
                .map(HolidayConfig::getFactor)
                .orElse(1.0);
    }
}
