package com.tinyfarm.service;

import com.tinyfarm.dto.FarmStatusResponse;
import com.tinyfarm.repository.FarmRepository;
import org.springframework.stereotype.Service;

@Service
public class FarmService {

    private final FarmRepository farmRepository;

    public FarmService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    public FarmStatusResponse getFarmStatus() {
        long farmCount = farmRepository.count();
        return new FarmStatusResponse("TinyFarm backend is ready.", farmCount);
    }
}
