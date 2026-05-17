package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.model.ReplenishmentRequest;
import com.inventory.model.dto.ReplenishmentSuggestion;
import com.inventory.model.enums.ReplenishmentStatus;
import com.inventory.service.ReplenishmentCalculationService;
import com.inventory.service.ReplenishmentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/replenishment")
public class ReplenishmentController {

    @Autowired
    private ReplenishmentCalculationService calculationService;

    @Autowired
    private ReplenishmentRequestService requestService;

    @Autowired
    private com.inventory.storage.InMemoryStorage storage;

    @GetMapping("/suggestions")
    public Result<List<ReplenishmentSuggestion>> getSuggestions(@RequestParam(required = false) String storeId) {
        return Result.success(calculationService.calculateSuggestions(storeId));
    }

    @PostMapping("/request")
    public Result<ReplenishmentRequest> createRequest(@RequestBody Map<String, Object> params) {
        try {
            String storeId = (String) params.get("storeId");
            String productId = (String) params.get("productId");
            Integer quantity = (Integer) params.get("quantity");
            return Result.success(requestService.createRequest(storeId, productId, quantity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/approve/{requestId}")
    public Result<ReplenishmentRequest> approveRequest(@PathVariable String requestId, @RequestBody Map<String, Object> params) {
        try {
            Integer quantity = (Integer) params.get("approvedQuantity");
            return Result.success(requestService.approveRequest(requestId, quantity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/reject/{requestId}")
    public Result<ReplenishmentRequest> rejectRequest(@PathVariable String requestId, @RequestBody Map<String, Object> params) {
        try {
            String remark = (String) params.get("remark");
            return Result.success(requestService.rejectRequest(requestId, remark));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/receive/{requestId}")
    public Result<ReplenishmentRequest> receiveStock(@PathVariable String requestId) {
        try {
            return Result.success(requestService.receiveStock(requestId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/requests")
    public Result<List<ReplenishmentRequest>> getRequests(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) ReplenishmentStatus status) {
        return Result.success(requestService.getRequests(storeId, status));
    }

    @GetMapping("/validate")
    public Result<Map<String, Object>> validateRequest(
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        Map<String, Object> result = new HashMap<>();
        int warehouseStock = requestService.getWarehouseStock(productId);
        int availableCapacity = requestService.getAvailableTransportCapacity();
        int maxCapacity = requestService.getMaxTransportCapacity();
        
        int lockedStock = storage.warehouseInventories.values().stream()
                .filter(inv -> inv.getProductId().equals(productId))
                .mapToInt(WarehouseInventory::getLockedStock)
                .findFirst()
                .orElse(0);

        result.put("warehouseStock", warehouseStock);
        result.put("warehouseLocked", lockedStock);
        result.put("warehouseSufficient", warehouseStock >= quantity);
        result.put("warehouseShortage", Math.max(0, quantity - warehouseStock));
        result.put("availableTransportCapacity", availableCapacity);
        result.put("maxTransportCapacity", maxCapacity);
        result.put("transportSufficient", availableCapacity >= quantity);
        result.put("transportShortage", Math.max(0, quantity - availableCapacity));
        result.put("canSubmit", warehouseStock >= quantity && availableCapacity >= quantity);

        return Result.success(result);
    }
}
