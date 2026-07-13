package com.example.module.springboottest.dto;

import com.example.module.springboottest.apigroup.Create;
import com.example.module.springboottest.apigroup.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentRequest {
    @NotNull(groups = {Update.class})
    private Integer id;
    @NotBlank(groups = {Create.class, Update.class})
    private String name;
    @NotNull(groups = {Create.class, Update.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
