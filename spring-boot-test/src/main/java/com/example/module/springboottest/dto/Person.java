package com.example.module.springboottest.dto;

import com.example.module.springboottest.annotation.Masked;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Person {
    private String name;
    private int age;
    @Masked(configKey = "email_masking_config", preserveEmailDomain = true)
    private String email;
    @Masked(configKey = "phone_masking_config")
    private String phone;
    private String address;
}
