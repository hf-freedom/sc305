package com.inventory.model;

public class StoreInventory {
    private String id;
    private String storeId;
    private String productId;
    private Integer availableStock;
    private Integer inTransitStock;
    private Integer last7DaysSales;
    private Integer last30DaysSales;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
    public Integer getInTransitStock() { return inTransitStock; }
    public void setInTransitStock(Integer inTransitStock) { this.inTransitStock = inTransitStock; }
    public Integer getLast7DaysSales() { return last7DaysSales; }
    public void setLast7DaysSales(Integer last7DaysSales) { this.last7DaysSales = last7DaysSales; }
    public Integer getLast30DaysSales() { return last30DaysSales; }
    public void setLast30DaysSales(Integer last30DaysSales) { this.last30DaysSales = last30DaysSales; }
}
