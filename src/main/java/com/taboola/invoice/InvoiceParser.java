package com.taboola.invoice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InvoiceParser {

    private static final int DIGIT_WIDTH = 3;
    private static final int DIGITS_PER_LINE = 9;
    private static final int LINE_LENGTH = DIGIT_WIDTH * DIGITS_PER_LINE;

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

    public static void parseFileAndWriteOutput(Path inputPath, Path outputPath) throws IOException {
        List<String> parsedInvoices = parseTextFile(inputPath);
        Files.write(outputPath, parsedInvoices);
    }

    public static List<String> parseTextFile(Path inputPath) throws IOException {
        List<String> inputLines = Files.readAllLines(inputPath);
        List<String> parsedInvoices = new ArrayList<>();

        for (int i = 0; i < inputLines.size(); i += 4) {
            String line1 = inputLines.get(i);
            String line2 = inputLines.get(i + 1);
            String line3 = inputLines.get(i + 2);
            String line4 = inputLines.get(i + 3);

            if (line1.length() != LINE_LENGTH || line2.length() != LINE_LENGTH || line3.length() != LINE_LENGTH) {
                throw new IllegalArgumentException("Each invoice line must be exactly 27 characters long");
            }

            if (!line4.isBlank()) {
                throw new IllegalArgumentException("The 4th line in each invoice block must be blank");
            }

            parsedInvoices.add(parseInvoiceEntry(line1, line2, line3));
        }

        return parsedInvoices;
    }

    private static String parseInvoiceEntry(String line1, String line2, String line3) {
        StringBuilder invoiceNumber = new StringBuilder();
        boolean containsIllegalDigit = false;

        for (int i = 0; i < LINE_LENGTH; i += DIGIT_WIDTH) {
            String digitPattern = line1.substring(i, i + 3)
                    + line2.substring(i, i + 3)
                    + line3.substring(i, i + 3);
            Character digit = DIGIT_MAP.get(digitPattern);
            if (digit == null) {
                invoiceNumber.append('?');
                containsIllegalDigit = true;
            } else {
                invoiceNumber.append(digit);
            }
        }

        return containsIllegalDigit ? invoiceNumber + " ILLEGAL" : invoiceNumber.toString();
    }

    public static void main(String[] args) throws IOException {
        // Question 1A
        parseFileAndWriteOutput(
                Path.of("src/main/resources/invoice/input_Q1a.txt"),
                Path.of("src/main/resources/invoice/code_output_Q1a_output.txt")
        );

        // Question 1B
        parseFileAndWriteOutput(
                Path.of("src/main/resources/invoice/input_Q1b.txt"),
                Path.of("src/main/resources/invoice/code_output_Q1b_output.txt")
        );
    }

}
