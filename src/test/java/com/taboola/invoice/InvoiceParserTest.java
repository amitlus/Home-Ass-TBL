package com.taboola.invoice;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class InvoiceParserTest {

    private final InvoiceParser sevenSegmentParser = new InvoiceParser(new SevenSegmentRecognizer());

    @Test
    void parsesValidInvoicesCorrectlyUsingSevenSegment() throws Exception {
        Path invoiceInputPath = Paths.get("src/test/resources/invoice/input_Q1a.txt");
        Path expectedParsedInvoicePath = Paths.get("src/test/resources/invoice/output_Q1a.txt");

        List<String> actualParsedInvoices = sevenSegmentParser.parseTextFile(invoiceInputPath);
        List<String> expectedParsedInvoices = Files.readAllLines(expectedParsedInvoicePath);

        assertThat(actualParsedInvoices)
                .as("Check that valid invoices are parsed correctly")
                .containsExactlyElementsOf(expectedParsedInvoices);
    }

    @Test
    void marksUnrecognizableDigitsAsIllegalUsingSevenSegment() throws Exception {
        Path invoiceInputPath = Paths.get("src/test/resources/invoice/input_Q1b.txt");
        Path expectedParsedInvoicePath = Paths.get("src/test/resources/invoice/output_Q1b.txt");

        List<String> actualParsedInvoices = sevenSegmentParser.parseTextFile(invoiceInputPath);
        List<String> expectedParsedInvoices = Files.readAllLines(expectedParsedInvoicePath);

        assertThat(actualParsedInvoices)
                .as("Check that invoices with illegal digits are marked correctly & replaced with '?'")
                .containsExactlyElementsOf(expectedParsedInvoices);
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

        assertThatThrownBy(() -> sevenSegmentParser.parseTextFile(input))
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

        assertThatThrownBy(() -> sevenSegmentParser.parseTextFile(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsOnIncompleteInvoiceBlockInSevenSegmentRecognizer() throws IOException {
        Path input = Files.createTempFile("invoice", ".txt");
        Files.write(input, List.of(
                " _  _  _  _  _  _  _  _  _ ",
                "| || || || || || || || || |",
                "|_||_||_||_||_||_||_||_||_|",
                "|_||_||_||_||_||_||_||_||_|",
                "--- not blank ---"
        ));

        assertThatThrownBy(() -> sevenSegmentParser.parseTextFile(input))
                .as("has 5 lines while seven-segment should have only 4.")
                .isInstanceOf(IllegalArgumentException.class);
    }

}
