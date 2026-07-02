package com.example.module.springbatch.batch;

import com.example.module.springbatch.entity.Employee;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeItemWriter implements ItemWriter<Employee> {

    private final EntityManager entityManager;

    @Override
    public void write(@NonNull Chunk<? extends Employee> chunk) {
        chunk.forEach(entityManager::persist);
        entityManager.flush();
    }
}
