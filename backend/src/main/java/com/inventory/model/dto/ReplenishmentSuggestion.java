package com.inventory.model.dto;

public class ReplenishmentSuggestion {
    private String storeId;
    private String storeName;
    private String productId;
    private String productName;
    private Integer suggestedQuantity;
    private Integer currentStock;
    private Integer avgDailySales;
    private Double priority;
    private String reason;
    private Double priorityFactor;
    private Double avg30DaysSales;
    private Boolean isSlowMoving;
    private String priorityReason;
    private Double holidayFactor;

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getSuggestedQuantity() { return suggestedQuantity; }
    public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    public Integer getAvgDailySales() { return avgDailySales; }
    public void setAvgDailySales(Integer avgDailySales) { this.avgDailySales = avgDailySales; }
    public Double getPriority() { return priority; }
    public void setPriority(Double priority) { this.priority = priority; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Double getPriorityFactor() { return priorityFactor; }
    public void setPriorityFactor(Double priorityFactor) { this.priorityFactor = priorityFactor; }
    public Double getAvg30DaysSales() { return avg30DaysSales; }
    public void setAvg30DaysSales(Double avg30DaysSales) { this.avg30DaysSales = avg30DaysSales; }
    public Boolean getIsSlowMoving() { return isSlowMoving; }
    public void setIsSlowMoving(Boolean isSlowMoving) { this.isSlowMoving = isSlowMoving; }
    public String getPriorityReason() { return priorityReason; }
    public void setPriorityReason(String priorityReason) { this.priorityReason = priorityReason; }
    public Double getHolidayFactor() { return holidayFactor; }
    public void setHolidayFactor(Double holidayFactor) { this.holidayFactor = holidayFactor; }
}
