package com.example.module.springredis.util;

import com.example.module.springredis.dto.GeoResponse;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisUtil {
    private final GeoOperations<String, String> geoOperations;
    private final JsonUtil jsonUtil;

    public RedisUtil(RedisTemplate<String, String> redisTemplate, JsonUtil jsonUtil) {
        this.geoOperations = redisTemplate.opsForGeo();
        this.jsonUtil = jsonUtil;
    }

    public void addGeoPoint(String key, double longitude, double latitude, Object value) {
        geoOperations.add(key, new Point(longitude, latitude), jsonUtil.objectToString(value));
    }

    public List<GeoResponse> getNearbyPoints(String key, double longitude, double latitude, Distance distance) {
        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOperations.search(
                key,
                GeoReference.fromCoordinate(new Point(longitude, latitude)),
                distance,
                args
        );

        List<GeoResponse> responses = new ArrayList<>();
        if (results != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                responses.add(new GeoResponse(result.getContent().getPoint(), result.getDistance(), result.getContent().getName()));
            }
        }
        return responses;
    }
}
