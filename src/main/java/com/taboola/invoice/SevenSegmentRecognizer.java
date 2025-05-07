package com.taboola.invoice;

import java.util.Map;

public class SevenSegmentRecognizer implements DigitRecognizer {
    public static final int DIGIT_WIDTH = 3;
    public static final int DIGIT_COUNT_PER_LINE = 9;
    public static final int DIGIT_LINE_COUNT = 3;
    public static final int LINES_PER_ENTRY = 4;


    private static final Map<String, Character> DIGIT_MAP = Map.ofEntries(
            Map.entry(" _ | ||_|", '0'),
            Map.entry("     |  |", '1'),
            Map.entry(" _  _||_ ", '2'),
            Map.entry(" _  _| _|", '3'),
            Map.entry("   |_|  |", '4'),
            Map.entry(" _ |_  _|", '5'),
            Map.entry(" _ |_ |_|", '6'),
            Map.entry(" _   |  |", '7'),
            Map.entry(" _ |_||_|", '8'),
            Map.entry(" _ |_| _|", '9')
    );

    @Override
    public char recognize(String asciiBlock) {
        return DIGIT_MAP.getOrDefault(asciiBlock, '?');
    }

    @Override
    public int getDigitWidth() {
        return DIGIT_WIDTH;
    }

    @Override
    public int getDigitsPerLine() {
        return DIGIT_COUNT_PER_LINE;
    }

    @Override
    public int getLinesPerEntry() {
        return LINES_PER_ENTRY;
    }

    @Override
    public int getDigitLineCount() {
        return DIGIT_LINE_COUNT;
    }

}
