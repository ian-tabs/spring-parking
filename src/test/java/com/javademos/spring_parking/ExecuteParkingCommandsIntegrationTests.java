package com.javademos.spring_parking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExecuteParkingCommandsIntegrationTest {

    @Autowired
    private ExecuteParkingCommands executeParkingCommands;

    private Path tempFile;

    @BeforeEach
    void setup() throws IOException {
        tempFile = Files.createTempFile("spring_parking_test_commands", ".txt");
        var commands = List.of(
                "create_parking_lot 6",
                "park ABC-1234 White",
                "park ABC-9999 White",
                "park ABC-0001 Black",
                "park ABC-7777 Red",
                "park ABC-2701 Blue",
                "park ABC-3141 Black",
                "leave 4",
                "status",
                "park ABC-333 White",
                "park ABC-9998 White",
                "plate_numbers_for_cars_with_colour White",
                "slot_numbers_for_cars_with_colour White",
                "slot_number_for_registration_number ABC-3141",
                "slot_number_for_registration_number ABD-1111"
        );
        Files.write(tempFile, commands);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldExecuteAllCommandsAndPrintExpectedOutput() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            executeParkingCommands.run(tempFile.toString());
        } finally {
            System.setOut(originalOut);
        }
        String output = outContent.toString().trim();

        System.out.println("\n=== TEST OUTPUT START ===");
        System.out.println(output);
        System.out.println("=== TEST OUTPUT END ===\n");

        String expectedOutput = String.join(System.lineSeparator(),
                "Created a parking lot with 6 slot(s)",
                "Allocated slot number: 1",
                "Allocated slot number: 2",
                "Allocated slot number: 3",
                "Allocated slot number: 4",
                "Allocated slot number: 5",
                "Allocated slot number: 6",
                "Slot number 4 is free",
                "Slot No. | Plate No | Colour",
                "\t1 | ABC-1234 | White",
                "\t2 | ABC-9999 | White",
                "\t3 | ABC-0001 | Black",
                "\t5 | ABC-2701 | Blue",
                "\t6 | ABC-3141 | Black",
                "Allocated slot number: 4",
                "Sorry, parking lot is full",
                "ABC-1234, ABC-9999, ABC-333",
                "1, 2, 4",
                "6",
                "Plate number ABD-1111 not found in parking lot"
        );
        assertThat(output).isEqualToNormalizingNewlines(expectedOutput);
    }

}
