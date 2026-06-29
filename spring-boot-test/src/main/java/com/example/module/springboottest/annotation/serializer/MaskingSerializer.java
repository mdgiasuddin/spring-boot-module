package com.example.module.springboottest.annotation.serializer;


import com.example.module.springboottest.annotation.Masked;
import com.example.module.springboottest.annotation.metadata.MaskMetadata;
import com.example.module.springboottest.annotation.util.SpringContextHolder;
import com.example.module.springboottest.service.FeatureConfigService;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MaskingSerializer extends ValueSerializer<String> {

    private final FeatureConfigService configService;
    private final MaskMetadata metadata;
    private final ValueSerializer<Object> defaultSerializer;

    // Do not remove this constructor
    public MaskingSerializer() {
        this.configService = SpringContextHolder.getBean(FeatureConfigService.class);
        this.metadata = null;
        this.defaultSerializer = null;
    }

    @SuppressWarnings("unchecked")
    public MaskingSerializer(FeatureConfigService configService, MaskMetadata metadata, ValueSerializer<?> defaultSerializer) {
        this.configService = configService;
        this.metadata = metadata;
        this.defaultSerializer = (ValueSerializer<Object>) defaultSerializer;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext ctxt) {
        if (value == null) {
            gen.writeNull();
            return;
        }

        if (metadata != null && configService.isMaskingEnabled(metadata.configKey())) {
            if (metadata.preserveEmailDomain() && value.contains("@")) {
                gen.writeString(maskEmail(value, metadata));
            } else {
                gen.writeString(maskStandardText(value, metadata));
            }
        } else if (defaultSerializer != null) {
            defaultSerializer.serialize(value, gen, ctxt);
        } else {
            gen.writeString(value);
        }
    }

    private String maskStandardText(String text, MaskMetadata metadata) {
        int len = text.length();
        if (len <= (metadata.keepPrefix() + metadata.keepSuffix())) {
            return String.valueOf(metadata.maskChar()).repeat(len);
        }
        return text.substring(0, metadata.keepPrefix()) +
                String.valueOf(metadata.maskChar()).repeat(len - metadata.keepPrefix() - metadata.keepSuffix()) +
                text.substring(len - metadata.keepSuffix());
    }

    private String maskEmail(String email, MaskMetadata metadata) {
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        int len = username.length();
        if (len <= (metadata.keepPrefix() + metadata.keepSuffix())) {
            return String.valueOf(metadata.maskChar()).repeat(len) + domain;
        }

        return username.substring(0, metadata.keepPrefix()) +
                String.valueOf(metadata.maskChar()).repeat(len - metadata.keepPrefix() - metadata.keepSuffix()) +
                username.substring(len - metadata.keepSuffix()) + domain;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        if (property != null) {
            Masked masked = property.getAnnotation(Masked.class);
            if (masked != null) {
                MaskMetadata meta = new MaskMetadata(
                        masked.configKey(),
                        masked.keepPrefix(),
                        masked.keepSuffix(),
                        masked.maskChar(),
                        masked.preserveEmailDomain()
                );

                ValueSerializer<?> standardSerializer = ctxt.findValueSerializer(property.getType());

                return new MaskingSerializer(this.configService, meta, standardSerializer);
            }
        }
        return this;
    }

}