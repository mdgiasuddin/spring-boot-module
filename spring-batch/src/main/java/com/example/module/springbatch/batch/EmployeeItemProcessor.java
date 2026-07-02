package com.example.module.springbatch.batch;

import com.example.module.springbatch.dto.EmployeeCsvDto;
import com.example.module.springbatch.entity.Employee;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeItemProcessor implements ItemProcessor<EmployeeCsvDto, Employee> {

    @Override
    public Employee process(@NonNull EmployeeCsvDto item) {
        return new Employee(item);
    }
}
