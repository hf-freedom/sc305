package com.inventory.model;

public class Product {
    private String id;
    private String name;
    private String category;
    private Double price;
    private Integer replenishmentCycle;
    private Integer safetyStock;
    private Double priorityFactor;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getReplenishmentCycle() { return replenishmentCycle; }
    public void setReplenishmentCycle(Integer replenishmentCycle) { this.replenishmentCycle = replenishmentCycle; }
    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }
    public Double getPriorityFactor() { return priorityFactor; }
    public void setPriorityFactor(Double priorityFactor) { this.priorityFactor = priorityFactor; }
}
