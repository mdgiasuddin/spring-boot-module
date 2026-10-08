package com.example.module.springredis.config;

import io.lettuce.core.protocol.ProtocolKeyword;

import java.nio.charset.StandardCharsets;

/**
 * RedisBloom commands as singletons. Lettuce keys its command latency metrics by
 * {@code commandType.toString()}, so command types must have a stable identity;
 * a new instance per call would create two histograms per invocation and leak memory.
 */
public enum BloomCommand implements ProtocolKeyword {

    BF_RESERVE("BF.RESERVE"),
    BF_ADD("BF.ADD"),
    BF_MADD("BF.MADD"),
    BF_EXISTS("BF.EXISTS");

    private final String command;
    private final byte[] bytes;

    BloomCommand(String command) {
        this.command = command;
        this.bytes = command.getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return command;
    }
}
