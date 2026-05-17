package com.inventory.model;

import com.inventory.model.enums.ReplenishmentStatus;
import java.time.LocalDateTime;

public class ReplenishmentRequest {
    private String id;
    private String storeId;
    private String productId;
    private Integer requestedQuantity;
    private Integer approvedQuantity;
    private ReplenishmentStatus status;
    private LocalDateTime requestTime;
    private LocalDateTime approveTime;
    private LocalDateTime receiveTime;
    private String remark;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Integer getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }
    public Integer getApprovedQuantity() { return approvedQuantity; }
    public void setApprovedQuantity(Integer approvedQuantity) { this.approvedQuantity = approvedQuantity; }
    public ReplenishmentStatus getStatus() { return status; }
    public void setStatus(ReplenishmentStatus status) { this.status = status; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    public LocalDateTime getApproveTime() { return approveTime; }
    public void setApproveTime(LocalDateTime approveTime) { this.approveTime = approveTime; }
    public LocalDateTime getReceiveTime() { return receiveTime; }
    public void setReceiveTime(LocalDateTime receiveTime) { this.receiveTime = receiveTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
