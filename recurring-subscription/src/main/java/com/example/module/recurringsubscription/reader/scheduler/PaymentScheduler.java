package com.example.module.recurringsubscription.reader.scheduler;

import com.example.module.recurringsubscription.common.entity.Subscription;
import com.example.module.recurringsubscription.common.repository.jdbc.SubscriptionBatchRepository;
import com.example.module.recurringsubscription.reader.service.SubscriptionService;
import com.example.module.recurringsubscription.util.ListUtil;
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

    @Scheduled(cron = "0 14 23 * * *")
    public void pay() {
        List<Subscription> subscriptions = subscriptionBatchRepository.fetchSubscriptions(LocalDate.now(), 3000);
        List<List<Subscription>> chunks = ListUtil.divideIntoChunks(subscriptions, 1000);

        try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
            chunks.forEach(chunk -> pool.submit(() -> subscriptionService.paySubscriptions(chunk)));
        }

    }
}
