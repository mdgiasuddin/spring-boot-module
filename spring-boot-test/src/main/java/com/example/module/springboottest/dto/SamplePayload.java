package com.example.module.springboottest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(doNotUseGetters = true)
public class SamplePayload {
    @JsonProperty("ID")
    private String id;
    @JsonProperty("MESSAGE")
    private String message;
    @JsonProperty("TIMESTAMP")
    private String timestamp;
}
