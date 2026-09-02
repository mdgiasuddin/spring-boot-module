package com.example.module.recurringsubscription.reader.scheduler;

import com.example.module.recurringsubscription.reader.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentScheduler {

    private static final int WORKERS = 4;
    private static final int BATCH_SIZE = 500;
    private static final int MAX_PER_WORKER = 5000;

    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 56 22 * * *")
    public void pay() {
        LocalDate dueDate = LocalDate.now();

        List<Callable<Integer>> workers = new ArrayList<>(WORKERS);
        for (int i = 0; i < WORKERS; i++) {
            workers.add(() -> drain(dueDate));
        }

        try (ExecutorService pool = Executors.newFixedThreadPool(WORKERS)) {
            List<Future<Integer>> futures = pool.invokeAll(workers);

            int queued = 0;
            int failed = 0;
            for (Future<Integer> future : futures) {
                try {
                    queued += future.get();
                } catch (ExecutionException e) {
                    failed++;
                    log.error("Payment worker failed", e.getCause());
                }
            }

            log.info("Queued {} payments for {} ({} of {} workers failed)",
                    queued, dueDate, failed, WORKERS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Payment run for {} was interrupted", dueDate);
        }
    }

    private int drain(LocalDate dueDate) {
        int processed = 0;

        while (processed < MAX_PER_WORKER) {
            int batch = subscriptionService.payNextBatch(dueDate, BATCH_SIZE);
            if (batch == 0) {
                return processed;
            }
            processed += batch;
        }

        log.warn("Worker hit the per-run cap of {}; the remainder will be picked up on the next run",
                MAX_PER_WORKER);
        return processed;
    }
}