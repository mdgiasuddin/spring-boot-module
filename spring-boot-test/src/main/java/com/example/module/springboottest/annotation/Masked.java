package com.example.module.springboottest.annotation;

import com.example.module.springboottest.annotation.serializer.MaskingSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import tools.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = MaskingSerializer.class)
public @interface Masked {
    String configKey();

    int keepPrefix() default 3;

    int keepSuffix() default 3;

    char maskChar() default '*';

    boolean preserveEmailDomain() default false; // New flag for emails
}
