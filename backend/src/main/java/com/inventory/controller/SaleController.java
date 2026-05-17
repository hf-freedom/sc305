package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.model.dto.SaleRequest;
import com.inventory.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public Result<Void> processSale(@RequestBody SaleRequest request) {
        try {
            saleService.processSale(request.getStoreId(), request.getProductId(), request.getQuantity());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
