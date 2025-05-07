package com.taboola.wordcounter;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WordsCounterTest {

    @Test
    void testWordCountingAcrossTheGivenFiles() {
        WordsCounter wc = new WordsCounter();

        wc.load(
                "src/test/resources/wordcounter/file1.txt",
                "src/test/resources/wordcounter/file2.txt",
                "src/test/resources/wordcounter/file3.txt"
        );

        Map<String, Integer> result = wc.getWordCountMap();

        assertThat(result)
                .containsEntry("and", 1)
                .containsEntry("file", 3)
                .containsEntry("first", 1)
                .containsEntry("is", 3)
                .containsEntry("one", 1)
                .containsEntry("second", 1)
                .containsEntry("the", 3)
                .containsEntry("third", 1)
                .containsEntry("this", 3);

        int totalWords = result.values().stream().mapToInt(Integer::intValue).sum();
        assertThat(totalWords).isEqualTo(17);
    }

    @Test
    void testWordCountingAcrossMultipleLargeFiles() {
        WordsCounter wc = new WordsCounter();

        wc.load(
                "src/test/resources/wordcounter/my_file1.txt",
                "src/test/resources/wordcounter/my_file2.txt",
                "src/test/resources/wordcounter/my_file3.txt"
        );

        Map<String, Integer> result = wc.getWordCountMap();

        assertThat(result)
                .containsEntry("the", 11)
                .containsEntry("your", 5)
                .containsEntry("i", 5)
                .containsEntry("productivity", 1)
                .containsEntry("quiet", 2);

        int totalWords = result.values().stream().mapToInt(Integer::intValue).sum();
        assertThat(totalWords).isEqualTo(268);
    }

}
