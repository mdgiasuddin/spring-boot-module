package com.example.module.recurringsubscription.util;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ListUtil {

    public static <T> List<List<T>> divideIntoChunks(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(new ArrayList<>(list.subList(i, Math.min(i + chunkSize, list.size()))));
        }

        return chunks;
    }
}
