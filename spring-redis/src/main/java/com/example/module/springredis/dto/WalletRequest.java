package com.example.module.springredis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletRequest {
    @NotBlank
    private String walletNumber;
}
