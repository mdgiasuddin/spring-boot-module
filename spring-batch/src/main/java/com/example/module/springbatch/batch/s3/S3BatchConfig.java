package com.example.module.springbatch.batch.s3;


import com.example.module.springbatch.batch.EmployeeItemProcessor;
import com.example.module.springbatch.dto.EmployeeCsvDto;
import com.example.module.springbatch.entity.Employee;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class S3BatchConfig {

    private final ResourceLoader resourceLoader;

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<EmployeeCsvDto> s3Reader(
            @Value("#{stepExecution.jobExecution.jobParameters.getString('s3FileUrl')}") String s3FileUrl
    ) {
        FlatFileItemReader<EmployeeCsvDto> reader = new FlatFileItemReaderBuilder<EmployeeCsvDto>()
                .name("s3Reader")
                .resource(resourceLoader.getResource(s3FileUrl))
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
    public JpaItemWriter<Employee> s3ToJpaWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Employee>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step s3EmployeeStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SynchronizedItemStreamReader<EmployeeCsvDto> reader,
            EmployeeItemProcessor processor,
            JpaItemWriter<Employee> writer,
            ThreadPoolTaskExecutor taskExecutor
    ) {
        return new StepBuilder("s3EmployeeStep", jobRepository)
                .<EmployeeCsvDto, Employee>chunk(50)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor s3TaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public Job s3InsertEmployeeJob(JobRepository jobRepository, Step subscriptionStep) {
        return new JobBuilder("s3InsertEmployeeJob", jobRepository)
                .start(subscriptionStep)
                .build();
    }
}

