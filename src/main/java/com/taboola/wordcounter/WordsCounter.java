package com.taboola.wordcounter;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WordsCounter {
    @Getter
    private final ConcurrentHashMap<String, Integer> wordCountMap = new ConcurrentHashMap<>();

    public void load(String... filenames) {
        ExecutorService executor = Executors.newWorkStealingPool();

        for (String filename : filenames) {
            executor.submit(() -> processFile(filename));
        }
        executor.shutdown();

        try {
            int timeoutMilliSeconds = Math.max(1, filenames.length) * 100;
            if (!executor.awaitTermination(timeoutMilliSeconds, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Parsing takes too long");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void processFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.lines()
                    .flatMap(line -> Arrays.stream(line.trim().split("\\s+")))
                    .filter(word -> !word.isEmpty())
                    .map(String::toLowerCase)
                    .forEach(this::countWord);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename + " - " + e.getMessage());
        }
    }

    private void countWord(String word) {
        Integer prev = wordCountMap.putIfAbsent(word, 1);
        if (prev != null) {
            wordCountMap.compute(word, (k, v) -> (v == null ? 1 : v + 1));
        }
    }

    public void displayStatus() {
        int total = 0;
        for (var entry : wordCountMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
            total += entry.getValue();
        }
        System.out.println("** total: " + total);
    }

    public static void main(String[] args) {
        WordsCounter wc = new WordsCounter();
        wc.load("src/main/resources/wordcounter/file1.txt",
                "src/main/resources/wordcounter/file2.txt",
                "src/main/resources/wordcounter/file3.txt");

        wc.displayStatus();
    }
}
