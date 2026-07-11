package com.example.module.recurringsubscription.reader.scheduler;

import com.example.module.recurringsubscription.common.entity.Subscription;
import com.example.module.recurringsubscription.common.repository.jdbc.SubscriptionBatchRepository;
import com.example.module.recurringsubscription.reader.service.SubscriptionService;
import com.example.module.recurringsubscription.util.Chunker;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class PaymentScheduler {

    private final SubscriptionBatchRepository subscriptionBatchRepository;
    private final SubscriptionService subscriptionService;
    private final Chunker chunker;

    @Scheduled(cron = "0 41 18 * * *")
    public void pay() {
        List<Subscription> subscriptions = subscriptionBatchRepository.fetchSubscriptions(LocalDate.now(), 10000);
        List<List<Subscription>> chunks = chunker.getChunks(subscriptions, 1000);

        try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
            chunks.forEach(chunk -> pool.submit(() -> subscriptionService.paySubscriptions(chunk)));
        }

    }
}
