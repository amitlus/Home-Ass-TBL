package com.taboola.invoice;

public class DigitRecognizerFactory {
    public static DigitRecognizer getRecognizer(String type) {
        return switch (type.toLowerCase()) {
            case "seven-segment" -> new SevenSegmentRecognizer();
            default -> throw new IllegalArgumentException("Unknown recognizer type: " + type);
        };
    }
}
