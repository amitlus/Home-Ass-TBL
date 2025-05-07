package com.taboola.invoice;

public interface DigitRecognizer {
    char recognize(String asciiBlock);

    int getDigitWidth();

    int getDigitsPerLine();

    int getLinesPerEntry(); // total number of lines (including any blank line)

    int getDigitLineCount(); // actual digit height (e.g. 3 for 7-segment)

    default int getLineLength() {
        return getDigitWidth() * getDigitsPerLine();
    }
}
