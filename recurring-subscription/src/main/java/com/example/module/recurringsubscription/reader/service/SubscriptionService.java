package com.example.module.recurringsubscription.reader.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.entity.Subscription;
import com.example.module.recurringsubscription.common.repository.jdbc.PaymentBatchRepository;
import com.example.module.recurringsubscription.common.repository.jdbc.SubscriptionBatchRepository;
import com.example.module.recurringsubscription.reader.publisher.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.QUEUED;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionBatchRepository subscriptionBatchRepository;
    private final PaymentBatchRepository paymentBatchRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public void paySubscriptions(List<Subscription> subscriptions) {
        List<Payment> payments = new ArrayList<>();
        for (Subscription subscription : subscriptions) {

            Payment payment = new Payment();
            payment.setSubscription(subscription);
            payment.setExecutionDate(subscription.getNextPaymentDate());
            payment.setAmount(subscription.getAmount());
            payment.setStatus(QUEUED);
            payments.add(payment);

            subscription.setNextPaymentDate(subscription.getNextPaymentDate().plusMonths(1));
        }

        subscriptionBatchRepository.batchUpdate(subscriptions);
        paymentBatchRepository.batchInsert(payments);

        kafkaProducer.sendPaymentEvent(payments);
    }
}
