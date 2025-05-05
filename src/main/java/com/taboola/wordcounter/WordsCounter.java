package com.taboola.wordcounter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordsCounter {
    private final ConcurrentHashMap<String, Integer> wordCountMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        WordsCounter wc = new WordsCounter();
        // Example file paths – update these with your actual paths or test inputs
        wc.load("file1.txt", "file2.txt", "file3.txt");
        wc.displayStatus();
    }

    public void load(String... filenames) {
        ExecutorService executor = Executors.newFixedThreadPool(filenames.length);

        for (String filename : filenames) {
            executor.submit(() -> processFile(filename));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(100); // wait for tasks to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted while waiting: " + e.getMessage());
            }
        }
    }

    private void processFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] words = line.trim().split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCountMap.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename + " - " + e.getMessage());
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
}
