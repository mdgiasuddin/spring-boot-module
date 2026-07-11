package com.example.module.recurringsubscription.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Chunker {

    public <T> List<List<T>> getChunks(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(new ArrayList<>(list.subList(i, Math.min(i + chunkSize, list.size()))));
        }

        return chunks;
    }
}
