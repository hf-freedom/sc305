package com.inventory.service;

import com.inventory.model.ReplenishmentRequest;
import com.inventory.model.WarehouseInventory;
import com.inventory.model.enums.ReplenishmentStatus;
import com.inventory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReplenishmentRequestService {

    @Autowired
    private InMemoryStorage storage;

    private static final int MAX_TRANSPORT_CAPACITY = 500;

    public ReplenishmentRequest createRequest(String storeId, String productId, Integer quantity) {
        WarehouseInventory warehouseInv = storage.warehouseInventories.values().stream()
                .filter(inv -> inv.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("仓库库存不存在"));

        if (warehouseInv.getAvailableStock() < quantity) {
            throw new RuntimeException("仓库库存不足，可用库存：" + warehouseInv.getAvailableStock());
        }

        int currentTotalRequested = storage.replenishmentRequests.values().stream()
                .filter(r -> r.getStatus() == ReplenishmentStatus.PENDING)
                .mapToInt(ReplenishmentRequest::getRequestedQuantity)
                .sum();

        if (currentTotalRequested + quantity > MAX_TRANSPORT_CAPACITY) {
            throw new RuntimeException("超出运输容量限制，剩余可用：" + (MAX_TRANSPORT_CAPACITY - currentTotalRequested));
        }

        ReplenishmentRequest request = new ReplenishmentRequest();
        request.setId(UUID.randomUUID().toString());
        request.setStoreId(storeId);
        request.setProductId(productId);
        request.setRequestedQuantity(quantity);
        request.setStatus(ReplenishmentStatus.PENDING);
        request.setRequestTime(LocalDateTime.now());
        storage.replenishmentRequests.put(request.getId(), request);

        return request;
    }

    public ReplenishmentRequest approveRequest(String requestId, Integer approvedQuantity) {
        ReplenishmentRequest request = storage.replenishmentRequests.get(requestId);
        if (request == null) {
            throw new RuntimeException("补货申请不存在");
        }

        WarehouseInventory warehouseInv = storage.warehouseInventories.values().stream()
                .filter(inv -> inv.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("仓库库存不存在"));

        if (warehouseInv.getAvailableStock() < approvedQuantity) {
            throw new RuntimeException("仓库库存不足");
        }

        warehouseInv.setAvailableStock(warehouseInv.getAvailableStock() - approvedQuantity);
        warehouseInv.setLockedStock(warehouseInv.getLockedStock() + approvedQuantity);

        storage.storeInventories.values().stream()
                .filter(inv -> inv.getStoreId().equals(request.getStoreId())
                        && inv.getProductId().equals(request.getProductId()))
                .findFirst()
                .ifPresent(inv -> {
                    inv.setInTransitStock(inv.getInTransitStock() + approvedQuantity);
                });

        request.setApprovedQuantity(approvedQuantity);
        request.setStatus(ReplenishmentStatus.APPROVED);
        request.setApproveTime(LocalDateTime.now());

        return request;
    }

    public ReplenishmentRequest rejectRequest(String requestId, String remark) {
        ReplenishmentRequest request = storage.replenishmentRequests.get(requestId);
        if (request == null) {
            throw new RuntimeException("补货申请不存在");
        }
        request.setStatus(ReplenishmentStatus.REJECTED);
        request.setRemark(remark);
        return request;
    }

    public ReplenishmentRequest receiveStock(String requestId) {
        ReplenishmentRequest request = storage.replenishmentRequests.get(requestId);
        if (request == null) {
            throw new RuntimeException("补货申请不存在");
        }

        WarehouseInventory warehouseInv = storage.warehouseInventories.values().stream()
                .filter(inv -> inv.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("仓库库存不存在"));

        warehouseInv.setLockedStock(warehouseInv.getLockedStock() - request.getApprovedQuantity());

        storage.storeInventories.values().stream()
                .filter(inv -> inv.getStoreId().equals(request.getStoreId())
                        && inv.getProductId().equals(request.getProductId()))
                .findFirst()
                .ifPresent(inv -> {
                    inv.setInTransitStock(Math.max(0, inv.getInTransitStock() - request.getApprovedQuantity()));
                    inv.setAvailableStock(inv.getAvailableStock() + request.getApprovedQuantity());
                });

        request.setStatus(ReplenishmentStatus.RECEIVED);
        request.setReceiveTime(LocalDateTime.now());

        return request;
    }

    public List<ReplenishmentRequest> getRequests(String storeId, ReplenishmentStatus status) {
        return storage.replenishmentRequests.values().stream()
                .filter(r -> storeId == null || r.getStoreId().equals(storeId))
                .filter(r -> status == null || r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public int getWarehouseStock(String productId) {
        return storage.warehouseInventories.values().stream()
                .filter(inv -> inv.getProductId().equals(productId))
                .mapToInt(WarehouseInventory::getAvailableStock)
                .findFirst()
                .orElse(0);
    }

    public int getAvailableTransportCapacity() {
        int usedCapacity = storage.replenishmentRequests.values().stream()
                .filter(r -> r.getStatus() == ReplenishmentStatus.PENDING)
                .mapToInt(ReplenishmentRequest::getRequestedQuantity)
                .sum();
        return MAX_TRANSPORT_CAPACITY - usedCapacity;
    }

    public int getMaxTransportCapacity() {
        return MAX_TRANSPORT_CAPACITY;
    }
}
