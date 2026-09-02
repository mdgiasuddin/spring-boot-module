# Payment Scheduler Slowdown — Investigation & Fixes

Conversation transcript, 2026-09-02. Module: `recurring-subscription`. Branch: `master`.

---

## Q1

> I have added version column in the payment table. Now my scheduler is taking 5 times
> longer to run in the recurring-subscription module. Is there any problem with the version
> column? Is there any problem in my coding?
>
> (attached: `PaymentScheduler.java`)

## A1

I found it, and confirmed it against your database. Here's the picture.

### The version column is *not* slowing down the scheduler's own SQL

The scheduler's write path never touches Hibernate for `Payment`. `SubscriptionService.paySubscriptions`
writes through raw JDBC (`PaymentBatchRepository.batchInsert`), and that INSERT lists only
`subscription_id, execution_date, amount, status` — no `version`. Your column is
`version integer NOT NULL DEFAULT 0`, so the insert works and the rows land with version 0. Zero added cost on that
path.

### The version column *is* breaking the consumer path — that's where the time went

Every single payment now throws an optimistic-lock failure. Evidence from your DB:

```
status,version,count
PROCESSING,1,3000
```

3000 payments, all stuck at `PROCESSING`/version 1. Not one reached `SUCCESS` or `FAILED`. Before you added
`@Version`, ~90% of these would be `SUCCESS`.

The cause is a load-in-one-transaction / save-in-another pattern that was harmless without a version column and is fatal
with one:

1. `PaymentHandler.handlePayment` (`PaymentHandler.java:21`) has no `@Transactional`. So
   `findById` runs in its own read-only transaction and hands back a **detached** `Payment`
   with version 0.
2. `paymentRepository.save(payment)` (`PaymentHandler.java:36`) issues
   `update ... set status='PROCESSING', version=1 where id=? and version=0`. DB row is now version 1. But **the return
   value is discarded** — your local `payment` variable is still the detached copy holding version 0.
3. `bankService.processPayment(payment)` gets that stale object and calls `save(payment)`
   again (`BankService.java:31`). Merge loads the row at version 1, sees the detached version 0, and throws
   `StaleObjectStateException` → `ObjectOptimisticLockingFailureException`.

The exception escapes the `@KafkaListener`, so Spring Kafka's error handler logs a full nested stack trace and
re-delivers the record. On re-delivery the `!QUEUED` guard at
`PaymentHandler.java:30` short-circuits and returns normally — so the record "succeeds" without ever being paid, and you
get no loud failure, just a slow run and 3000 payments frozen in
`PROCESSING`.

The wall-clock cost per payment: a doubled delivery, a long stack trace written to a console that also has
`show-sql: true` + `format_sql: true`, extra DB round trips, and **a wasted rate-limiter token** — and your bucket is
only 50 tokens/sec, so tokens are the hard bottleneck for the whole batch.

### Your other uncommitted change multiplies it

`PaymentEventListener` now has `listen`, `listen2`, `listen3` — same topic, same `groupId`, and the topic has 3
partitions. That's 3 consumers in one JVM instead of 1, running in parallel, all generating failing SQL and stack
traces, all contending for the same Hikari pool (default max 10)
and the same synchronized console appender that your scheduler threads are writing 3000
`log.info("Message sent -> ...")` lines into.

Worth knowing: those extra listeners buy you **no** throughput anyway. `RedisRateLimiter` is a single global bucket at
50/sec, so 3 consumers just queue up behind the same limit and add contention.

### Fixes

Smallest change that stops the exception — use what `save` returns:

```java
payment.setStatus(PROCESSING);

Payment claimed = paymentRepository.save(payment);   // managed copy, version 1
bankService.

processPayment(claimed);
```

Better, use a wrapper type so Hibernate can distinguish transient from detached:

```java
@Version
private Integer version;   // not `int version = 0`
```

And a cleaner claim that sidesteps optimistic locking for the state transition entirely:

```java
@Modifying
@Query("update Payment p set p.status = :to where p.id = :id and p.status = :from")
int claim(Long id, PaymentStatus from, PaymentStatus to);
```

Then drop `listen2`/`listen3` and use `@KafkaListener(..., concurrency = "3")` on the single method if you want
per-partition parallelism, and turn off `show-sql`/`format_sql` for batch runs.

### Separate bugs in PaymentScheduler.java

1. **`for update skip locked` is a no-op.** `pay()` has no `@Transactional`, so
   `jdbcTemplate.query` runs in autocommit — Postgres commits the implicit transaction the moment the SELECT finishes
   and releases every row lock. Two instances of this scheduler would happily fetch the same 3000 rows and
   double-charge. The fetch and the `batchUpdate` need to be in one transaction for the locking to mean anything.
2. **Exceptions are silently swallowed** (`PaymentScheduler.java:29`). `pool.submit(...)` returns a `Future` you
   discard, so anything thrown inside `paySubscriptions` disappears without a log line. Use `invokeAll` and inspect the
   futures, or wrap the task body in try/catch with logging.
3. **Rows past the limit are orphaned.** `where next_payment_date = ?` with `limit 3000`: if more than 3000
   subscriptions are due today, the surplus is never picked up — tomorrow's run queries tomorrow's date, so those rows
   sit at today's date forever. Use `<=` on the date, or loop until the fetch comes back empty.
4. Minor: `newFixedThreadPool(10)` for a fixed 3 chunks creates 7 threads that never run anything, and allocates a fresh
   pool on every invocation.

---

## Q2

> where do I use this one: `int claim(Long id, PaymentStatus from, PaymentStatus to)`
>
> (attached: `PaymentRepository.java`)

## A2

`claim` goes in the repository interface, and it replaces the read-check-write block inside
`PaymentHandler.handlePayment`.

spring-boot-starter-parent 4.1.0 sets `-parameters`, so parameter names resolve — but I'll use
`@Param` anyway since it never breaks.

### 1. Declare it in `PaymentRepository.java`

```java
package com.example.module.recurringsubscription.common.repository.jpa;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.enumeration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Transactional
    @Query("update Payment p set p.status = :to where p.id = :id and p.status = :from")
    int claim(@Param("id") Long id,
              @Param("from") PaymentStatus from,
              @Param("to") PaymentStatus to);
}
```

The `@Transactional` is required, not optional. `SimpleJpaRepository` is annotated
`@Transactional(readOnly = true)` at class level, and your interface method inherits that — an update query on a
read-only transaction fails at runtime.

### 2. Call it in `PaymentHandler.java`, replacing lines 22–36

```java
public void handlePayment(Long paymentId) {
    int claimed = paymentRepository.claim(paymentId, QUEUED, PROCESSING);

    if (claimed == 0) {
        log.warn("Payment with id: {} not found or already claimed", paymentId);
        return;
    }

    Payment payment = paymentRepository.findById(paymentId).orElseThrow();
    bankService.processPayment(payment);
}
```

That single statement replaces the `findById` → check `QUEUED` → `setStatus` → `save` sequence. The
`where ... and p.status = :from` predicate does in one atomic DB statement what your three lines did across two
transactions.

### Why this fixes the slowdown

- **The stale-version merge is gone.** Previously the first `save()` bumped the row to version 1 while your local
  `payment` stayed at version 0, so `BankService`'s `save()` always threw. Now the only `save()` in the flow is
  `BankService`'s, and it operates on an entity read *after* the claim, so its version matches the row.
- **A JPQL bulk update does not touch `@Version`.** The row stays at version 0 after `claim`, which is exactly why the
  subsequent `findById` → `save` in `BankService` succeeds cleanly (0 → 1).
- **The read-check-write race is closed.** Your current `findById` → check status → `save` lets two consumers both read
  `QUEUED` and both proceed. `claim` returns 1 to exactly one caller; everyone else gets 0. That matters directly for
  your three duplicate listeners, and for running more than one app instance.

### Two things to watch

**Don't put `@Transactional` on `handlePayment`.** Two reasons. First,
`bankService.processPayment` blocks in `Thread.sleep` waiting for rate-limiter tokens (`BankService.java:36`) — holding
a DB transaction open across that sleep would pin a Hikari connection for the whole wait and starve your pool. Second, a
`@Modifying` query bypasses the persistence context, so if `claim` and `findById` shared one transaction the re-read
would return a cached entity still showing `QUEUED`. Leaving `handlePayment` non-transactional gives `findById`
a fresh persistence context. If you ever do need them in one transaction, you'd have to use
`@Modifying(clearAutomatically = true)`.

**The extra `findById` is a second round trip.** If you want to avoid it, have `claim` return nothing and instead pass
the amount and subscription through the Kafka event so `BankService` never needs the entity — but that's a bigger
change, and one round trip per message is not your bottleneck. Your bottleneck is the 50-token/sec Redis bucket.

Note this makes `@Version` on `Payment` mostly redundant for this flow — the status CAS is the real guard. Keeping it is
harmless, but switch it to `Integer` rather than `int` as mentioned above, so Hibernate can tell transient from
detached.

---

## Q3

> Provide fix for this: Separate bugs in PaymentScheduler.java (items 1–4 above)
>
> (attached: `PaymentHandler.java`)

## A3

Nothing else calls those methods, so the scheduler is the only caller — that gives us room to restructure.

### First, why #1 can't be fixed in place

Wrapping `pay()` in `@Transactional` would *deadlock*, not fix it. The `FOR UPDATE` locks would be held by `pay()`'s
transaction on `pay()`'s connection, while the `UPDATE subscription ...` runs in a worker thread's *separate*
transaction. The worker would block waiting for a lock that `pay()`
holds — and `pay()` won't commit until the worker finishes, because `close()` awaits termination.

So the fetch has to move *into* the worker's transaction. Once you do that, `SKIP LOCKED` does what it was designed
for — N workers each claim a disjoint batch — and that also solves #3 for free:
each worker loops until its fetch comes back empty.

### 1. `SubscriptionBatchRepository.java:33` — pick up arrears

```java
where next_payment_date <= ?
```

### 2. `SubscriptionService.java` — claim and process in one transaction

Replace `paySubscriptions(List<Subscription>)` with a method that owns the fetch, and return the count so the caller
knows whether to keep going:

```java

@Transactional
public int payNextBatch(LocalDate dueDate, int batchSize) {
    List<Subscription> subscriptions = subscriptionBatchRepository.fetchSubscriptions(dueDate, batchSize);

    if (subscriptions.isEmpty()) {
        return 0;
    }

    List<Payment> payments = new ArrayList<>(subscriptions.size());
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

    return subscriptions.size();
}
```

The `JdbcTemplate` calls now join this transaction — `JpaTransactionManager` binds the JDBC connection to the
`DataSource`, and `JdbcTemplate` picks it up via `DataSourceUtils.getConnection`. Same connection, same transaction, so
the `FOR UPDATE SKIP LOCKED` locks taken by the SELECT are still held when the UPDATE runs and are released only at
commit. That's what makes the locking real.

### 3. `PaymentScheduler.java` — full replacement

```java
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

    @Scheduled(cron = "0 24 15 * * *")
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
```

What each part addresses:

- **#1** — the fetch is inside `payNextBatch`'s transaction, so the locks survive until commit. Four workers now
  genuinely partition the work via `SKIP LOCKED`, and a second application instance can't grab the same rows.
- **#2** — `invokeAll` blocks until all workers finish and hands back futures; `future.get()`
  rethrows anything the worker threw as `ExecutionException`, which gets logged with its
  `getCause()`. Nothing disappears silently anymore. Note a worker that throws stops draining rather than retrying the
  same batch forever — deliberate, since a deterministic failure would otherwise spin.
- **#3** — `drain` loops until its fetch returns empty, and the repository now uses `<=`, so nothing is orphaned.
  `MAX_PER_WORKER` is a safety valve that's only safe *because* of the `<=`
  change: leftovers are picked up on the next run instead of being stranded.
- **#4** — exactly `WORKERS` tasks for a `WORKERS`-sized pool, no idle threads. The pool is still allocated per run,
  which is fine for a once-a-day job — better than parking 4 threads for 24 hours. If you'd rather not allocate, inject
  a Spring `TaskExecutor` bean instead.

`subscriptionBatchRepository` and the `ListUtil` import drop out of the scheduler, and
`ListUtil.divideIntoChunks` becomes unused project-wide.

### One thing you should decide

**`<=` changes the billing semantics.** A subscription three months in arrears now advances one month per batch and
stays `<= today`, so it gets refetched within the same run and charged three times — once per missed period. That's
defensible for a subscription, and it terminates (every commit strictly advances the date). But if you'd rather charge
at most once per run and just catch the date up, that needs a different `nextPaymentDate` calculation.

### Related, and worth doing while you're in here

`kafkaProducer.sendPaymentEvent(payments)` runs *inside* the transaction, and it does 500 sends plus 500 `log.info`
calls while holding `FOR UPDATE` row locks. That was already true before, but it matters more now that the locks are
actually held — it directly serializes your four workers. Moving it after commit both shortens lock hold time and stops
you from publishing events for a batch that rolls back (which is what produces those `Payment with id: {} not found`
warnings):

```java
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override
    public void afterCommit() {
        kafkaProducer.sendPaymentEvent(payments);
    }
});
```

---

## Appendix — evidence gathered

Schema of `payment` in `recurring_subscription` (Postgres on `localhost:5435`):

```
column_name,data_type,is_nullable,column_default
id,bigint,NO,
execution_date,date,NO,
status,character varying,NO,
subscription_id,bigint,NO,
amount,numeric,NO,
version,integer,NO,0
```

Row state after the slow run:

```sql
select status, version, count(*) from payment group by status, version order by status, version;
```

```
status,version,count
PROCESSING,1,3000
```

Relevant configuration (`application.yaml`):

- `spring.jpa.show-sql: true`, `hibernate.format_sql: true` — every consumer statement is printed as a formatted
  multi-line block to a synchronized console appender.
- `spring.jpa.hibernate.ddl-auto: none` — schema is managed by hand.
- No `spring.datasource.hikari.maximum-pool-size` — default is 10.

Other relevant constants:

- `KafkaConfig.SUBSCRIPTION_PAYMENTS` topic: 3 partitions.
- `RedisRateLimiter`: `MAX_TOKENS = 50`, `REFILL_RATE = 50` — a single global token bucket, the hard ceiling on
  end-to-end batch throughput.

### Summary of pending changes

| File                               | Change                                | Reason                                             |
|------------------------------------|---------------------------------------|----------------------------------------------------|
| `PaymentHandler.java`              | replace read-check-write with `claim` | fixes the optimistic-lock failure on every payment |
| `PaymentRepository.java`           | add `@Modifying` `claim` method       | atomic status CAS, closes the race                 |
| `Payment.java`                     | `int version = 0` → `Integer version` | lets Hibernate distinguish transient from detached |
| `PaymentEventListener.java`        | delete `listen2`, `listen3`           | duplicates; no throughput gain, adds contention    |
| `PaymentScheduler.java`            | rewrite as draining workers           | scheduler bugs 1–4                                 |
| `SubscriptionService.java`         | `paySubscriptions` → `payNextBatch`   | moves the fetch into the transaction               |
| `SubscriptionBatchRepository.java` | `next_payment_date <= ?`              | stops orphaning overdue rows                       |
| `application.yaml`                 | disable `show-sql` / `format_sql`     | console I/O is a real cost at batch scale          |

None of these have been applied — the working tree still has the original code plus the three duplicate listeners and
the `@Version` field.
