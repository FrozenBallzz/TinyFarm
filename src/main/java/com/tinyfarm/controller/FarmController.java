package com.tinyfarm.controller;

import com.tinyfarm.dto.FarmStatusResponse;
import com.tinyfarm.service.FarmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farm")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping
    public FarmStatusResponse getFarmStatus() {
        return farmService.getFarmStatus();
    }
}
