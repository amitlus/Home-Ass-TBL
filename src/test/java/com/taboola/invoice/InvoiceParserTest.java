package com.taboola.invoice;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class InvoiceParserTest {

    @Test
    void parsesValidInvoicesCorrectly() throws Exception {
        Path inputPath = Paths.get("src/test/resources/invoice/input_Q1a.txt");
        Path expectedOutputPath = Paths.get("src/test/resources/invoice/output_Q1a.txt");

        List<String> actualOutput = InvoiceParser.parseTextFile(inputPath);
        List<String> expectedOutput = Files.readAllLines(expectedOutputPath);

        assertThat(actualOutput)
                .as("Check that valid invoices are parsed correctly")
                .containsExactlyElementsOf(expectedOutput);
    }

    @Test
    void marksUnrecognizableDigitsAsIllegal() throws Exception {
        Path inputPath = Paths.get("src/test/resources/invoice/input_Q1b.txt");
        Path expectedOutputPath = Paths.get("src/test/resources/invoice/output_Q1b.txt");

        List<String> actualOutput = InvoiceParser.parseTextFile(inputPath);
        List<String> expectedOutput = Files.readAllLines(expectedOutputPath);

        assertThat(actualOutput)
                .as("Check that invoices with illegal digits are marked correctly & replaced with '?")
                .containsExactlyElementsOf(expectedOutput);
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

        assertThatThrownBy(() -> InvoiceParser.parseTextFile(input))
                .isInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> InvoiceParser.parseTextFile(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
