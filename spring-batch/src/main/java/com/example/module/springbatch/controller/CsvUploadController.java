package com.example.module.springbatch.controller;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvUploadController {

    private final S3Template s3Template;
    private final JobOperator jobOperator;
    private final Job s3InsertEmployeeJob;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;


    @PostMapping("/employees")
    public ResponseEntity<String> uploadAndProcessCsv(@RequestParam("file") MultipartFile file) {
        try {
            String s3Key = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            s3Template.upload(bucketName, s3Key, file.getInputStream());
            String s3Url = String.format("s3://%s/%s", bucketName, s3Key);

            // 3. Build a proper JobParameters object
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("s3FileUrl", s3Url)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // 4. Spring Batch 6 syntax: pass the Job instance and JobParameters object
            // This returns a JobExecution object
            var jobExecution = jobOperator.start(s3InsertEmployeeJob, jobParameters);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("File uploaded. Job started with Execution ID: " + jobExecution.getId() + ". Resource: " + s3Url);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload and processing failed: " + e.getMessage());
        }
    }

}
