package com.example.module.springboottest.annotation.metadata;

public record MaskMetadata(
        String configKey,
        int keepPrefix,
        int keepSuffix,
        char maskChar,
        boolean preserveEmailDomain
) {
}