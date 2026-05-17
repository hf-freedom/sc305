package com.inventory.model;

import java.time.LocalDate;

public class HolidayConfig {
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double factor;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Double getFactor() { return factor; }
    public void setFactor(Double factor) { this.factor = factor; }
}
