package com.example.module.recurringsubscription.processor.ratelimiter;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * One FAIR lock per merchant, used to serialize threads that are waiting for a rate-limit token.
 * <p>
 * Without it, every waiting thread polls Redis on its own and they all wake at the same moment
 * (thundering herd, random winner). With it, only the lock holder polls Redis; the others park
 * with zero CPU and zero Redis calls, and get their turn in FIFO order.
 * <p>
 * Scope: per application instance. Each instance still has its own holder per merchant, so
 * Redis sees at most (active merchants x instances) pollers instead of one per thread.
 * <p>
 * Entries are reference-counted and removed when nobody holds or waits for them,
 * so the map never grows with the number of merchants ever seen.
 */
@Component
public class MerchantLockRegistry {

    private static final class Entry {
        final ReentrantLock lock = new ReentrantLock(true);   // fair = FIFO hand-off
        int refs;                                              // only touched inside map.compute()
    }

    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

    /**
     * Blocks (interruptible) until this thread holds the merchant's lock. Close the lease to release.
     */
    public Lease acquire(String merchantId) throws InterruptedException {
        Entry entry = entries.compute(merchantId, (k, e) -> {
            Entry x = (e == null) ? new Entry() : e;
            x.refs++;
            return x;
        });
        try {
            entry.lock.lockInterruptibly();
        } catch (InterruptedException ex) {
            release(merchantId);                               // give back the reference we took
            throw ex;
        }
        return new Lease(merchantId, entry);
    }

    private void release(String merchantId) {
        entries.compute(merchantId, (k, e) -> (e == null || --e.refs <= 0) ? null : e);
    }

    /**
     * Number of merchants currently held or waited on (for metrics/tests).
     */
    public int size() {
        return entries.size();
    }

    public final class Lease implements AutoCloseable {
        private final String merchantId;
        private final Entry entry;

        private Lease(String merchantId, Entry entry) {
            this.merchantId = merchantId;
            this.entry = entry;
        }

        @Override
        public void close() {
            entry.lock.unlock();
            release(merchantId);
        }
    }
}
