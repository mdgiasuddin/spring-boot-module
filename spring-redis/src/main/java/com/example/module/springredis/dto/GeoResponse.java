package com.example.module.springredis.dto;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

public record GeoResponse(
        Point point,
        Distance distance,
        String value
) {
}
