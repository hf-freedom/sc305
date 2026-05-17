package com.inventory.model.dto;

public class StockAlert {
    private String storeId;
    private String storeName;
    private String productId;
    private String productName;
    private Integer currentStock;
    private Integer safetyStock;
    private Integer shortageQuantity;
    private String level;

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }
    public Integer getShortageQuantity() { return shortageQuantity; }
    public void setShortageQuantity(Integer shortageQuantity) { this.shortageQuantity = shortageQuantity; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
