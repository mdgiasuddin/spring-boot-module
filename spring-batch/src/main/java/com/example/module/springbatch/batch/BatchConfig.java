package com.example.module.springbatch.batch;


import com.example.module.springbatch.dto.EmployeeCsvDto;
import com.example.module.springbatch.entity.Employee;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<EmployeeCsvDto> reader() {
        return new FlatFileItemReaderBuilder<EmployeeCsvDto>()
                .name("employeeFileReader")
                .resource(new ClassPathResource("csv/employees.csv"))
                .linesToSkip(1)
                .delimited()
                .names("name", "department", "email", "salary", "dob")
                .targetType(EmployeeCsvDto.class)
                .build();
    }

    @Bean
    public Step employeeStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<EmployeeCsvDto> reader,
            EmployeeItemProcessor processor,
            EmployeeItemWriter writer
    ) {
        return new StepBuilder("insertEmployeeStep", jobRepository)
                .<EmployeeCsvDto, Employee>chunk(100)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    //    @Bean
    public Job insertEmployeeJob(JobRepository jobRepository, Step subscriptionStep) {
        return new JobBuilder("insertEmployeeJob", jobRepository)
                .start(subscriptionStep)
                .build();
    }
}

