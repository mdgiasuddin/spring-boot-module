package com.example.module.springredis.controller;

import com.example.module.springredis.dto.WalletRequest;
import com.example.module.springredis.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bloom")
@RequiredArgsConstructor
public class BloomController {

    private final BloomFilterService bloomFilterService;

    @PostMapping("/add")
    public String addWallet(
            @RequestBody WalletRequest request
    ) {
        boolean added = bloomFilterService.addWallet(request.getWalletNumber());

        return added ? "Wallet added" : "Failed to add wallet";
    }

    @GetMapping("/check/{walletNumber}")
    public boolean checkWallet(
            @PathVariable String walletNumber
    ) {
        return bloomFilterService.mightExist(walletNumber);
    }

    @GetMapping("/rebuild")
    public void rebuildBloomFilter() {
        bloomFilterService.rebuildBloomFilter();
    }
}
