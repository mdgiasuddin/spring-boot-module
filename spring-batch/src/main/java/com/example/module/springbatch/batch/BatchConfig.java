package com.example.module.springbatch.batch;


import com.example.module.springbatch.dto.EmployeeCsvDto;
import com.example.module.springbatch.entity.Employee;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.infrastructure.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public SynchronizedItemStreamReader<EmployeeCsvDto> reader() {
        FlatFileItemReader<EmployeeCsvDto> reader = new FlatFileItemReaderBuilder<EmployeeCsvDto>()
                .name("employeeFileReader")
                .resource(new ClassPathResource("csv/employees.csv"))
                .linesToSkip(1)
                .delimited()
                .names("name", "department", "email", "salary", "dob")
                .targetType(EmployeeCsvDto.class)
                .build();
        return new SynchronizedItemStreamReaderBuilder<EmployeeCsvDto>()
                .delegate(reader)
                .build();
    }

    @Bean
    public JpaItemWriter<Employee> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Employee>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step employeeStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SynchronizedItemStreamReader<EmployeeCsvDto> reader,
            EmployeeItemProcessor processor,
            JpaItemWriter<Employee> writer,
            ThreadPoolTaskExecutor taskExecutor
    ) {
        return new StepBuilder("insertEmployeeStep", jobRepository)
                .<EmployeeCsvDto, Employee>chunk(50)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public Job insertEmployeeJob(JobRepository jobRepository, Step subscriptionStep) {
        return new JobBuilder("insertEmployeeJob", jobRepository)
                .start(subscriptionStep)
                .build();
    }
}

