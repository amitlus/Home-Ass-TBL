package com.taboola.invoice;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class InvoiceParser {

    private final DigitRecognizer recognizer;

    public List<String> parseTextFile(Path inputPath) throws IOException {
        List<String> parsedInvoices = new ArrayList<>();

        int digitWidth = recognizer.getDigitWidth();
        int lineLength = recognizer.getLineLength();
        int digitLines = recognizer.getDigitLineCount();
        int linesPerEntry = recognizer.getLinesPerEntry();

        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            List<String> lines;
            while ((lines = readInvoiceBlock(reader, linesPerEntry)) != null) {
                if (lines.size() < linesPerEntry) {
                    throw new IllegalArgumentException("Incomplete invoice block: expected " + linesPerEntry + " lines, but got " + lines.size());
                }

                for (int i = 0; i < digitLines; i++) {
                    if (lines.get(i).length() != lineLength) {
                        throw new IllegalArgumentException("Each invoice line must be exactly " + lineLength + " characters long");
                    }
                }
                if (!lines.get(linesPerEntry - 1).isBlank()) {
                    throw new IllegalArgumentException("The last line in each invoice block must be blank");
                }

                parsedInvoices.add(parseInvoiceEntry(lines.subList(0, digitLines), digitWidth, lineLength));
            }
        }

        return parsedInvoices;
    }

    private static List<String> readInvoiceBlock(BufferedReader reader, int linesPerEntry) throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < linesPerEntry; i++) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            lines.add(line);
        }
        return lines.isEmpty() ? null : lines;
    }

    private String parseInvoiceEntry(List<String> digitLines, int digitWidth, int lineLength) {
        StringBuilder invoiceNumber = new StringBuilder();
        boolean containsIllegalDigit = false;

        for (int i = 0; i < lineLength; i += digitWidth) {
            StringBuilder digitPattern = new StringBuilder();
            for (String line : digitLines) {
                digitPattern.append(line, i, i + digitWidth);
            }
            char digit = recognizer.recognize(digitPattern.toString());
            if (digit == '?') {
                containsIllegalDigit = true;
            }
            invoiceNumber.append(digit);
        }

        return containsIllegalDigit ? invoiceNumber + " ILLEGAL" : invoiceNumber.toString();
    }

    public static void parseAndWriteWithSevenSegmentRecognizer(Path inputPath, Path outputPath) throws IOException {
        InvoiceParser parser = new InvoiceParser(DigitRecognizerFactory.getRecognizer("seven-segment"));
        List<String> parsedInvoices = parser.parseTextFile(inputPath);
        Files.write(outputPath, parsedInvoices);
    }

    public static void main(String[] args) throws IOException {
        // Question 1A
        parseAndWriteWithSevenSegmentRecognizer(
                Path.of("src/main/resources/invoice/input_Q1a.txt"),
                Path.of("src/main/resources/invoice/code_output_Q1a_output.txt")
        );

        // Question 1B
        parseAndWriteWithSevenSegmentRecognizer(
                Path.of("src/main/resources/invoice/input_Q1b.txt"),
                Path.of("src/main/resources/invoice/code_output_Q1b_output.txt")
        );
    }
}
