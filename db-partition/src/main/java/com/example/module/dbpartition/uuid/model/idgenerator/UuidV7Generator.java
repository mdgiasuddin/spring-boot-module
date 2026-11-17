package com.example.module.dbpartition.uuid.model.idgenerator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Component
public final class UuidV7Generator {

    private static final Random RANDOM = new SecureRandom();

    private long lastMillis = -1;
    private int counter;                       // 12 bits, keeps IDs ordered within a millisecond

    public synchronized UUID nextId() {
        long now = System.currentTimeMillis();

        if (now > lastMillis) {
            lastMillis = now;
            counter = RANDOM.nextInt(1 << 11); // random start, leaves headroom to increment
        } else {
            // same millisecond, or the clock moved backwards: keep the last timestamp
            // and increment, so IDs never go backwards
            if (++counter > 0xFFF) {           // counter overflow: borrow the next millisecond
                lastMillis++;
                counter = RANDOM.nextInt(1 << 11);
            }
        }

        long msb = (lastMillis << 16)          // 48-bit timestamp
                | (0x7L << 12)               // version 7
                | counter;                    // 12-bit counter

        long lsb = (RANDOM.nextLong() & 0x3FFF_FFFF_FFFF_FFFFL)
                | 0x8000_0000_0000_0000L;     // variant 10

        return new UUID(msb, lsb);
    }
}
