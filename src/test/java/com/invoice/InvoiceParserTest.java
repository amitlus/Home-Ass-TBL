package com.invoice;

import com.taboola.invoice.InvoiceParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceParserTest {

    @Test
    public void parsesValidInvoicesCorrectly() throws Exception {
        Path inputPath = Paths.get("src/test/resources/input_Q1a.txt");
        Path expectedOutputPath = Paths.get("src/test/resources/output_Q1a.txt");

        List<String> actualOutput = InvoiceParser.parseTextFile(inputPath);
        List<String> expectedOutput = Files.readAllLines(expectedOutputPath);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void marksUnrecognizableDigitsAsIllegal() throws Exception {
        Path inputPath = Paths.get("src/test/resources/input_Q1b.txt");
        Path expectedOutputPath = Paths.get("src/test/resources/output_Q1b.txt");

        List<String> actualOutput = InvoiceParser.parseTextFile(inputPath);
        List<String> expectedOutput = Files.readAllLines(expectedOutputPath);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void throwsOnLineTooShort() throws IOException {
        Path input = Files.createTempFile("invoice", ".txt");
        Files.write(input, List.of(
                " _  _  _  _  _  _  _  _ ",  // only 24 chars
                "| || || || || || || || |",
                "|_||_||_||_||_||_||_||_|",
                ""
        ));

        assertThrows(IllegalArgumentException.class, () -> {
            InvoiceParser.parseTextFile(input);
        });
    }

    @Test
    void throwsOnNonBlankSeparatorLine() throws IOException {
        Path input = Files.createTempFile("invoice", ".txt");
        Files.write(input, List.of(
                " _  _  _  _  _  _  _  _  _ ",
                "| || || || || || || || || |",
                "|_||_||_||_||_||_||_||_||_|",
                "--- not blank ---"
        ));

        assertThrows(IllegalArgumentException.class, () -> {
            InvoiceParser.parseTextFile(input);
        });
    }
}
