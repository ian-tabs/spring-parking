package com.javademos.spring_parking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.List;

@Component
public class ExecuteParkingCommands implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java -jar parking-lot.jar <parking commands file>");
            return;
        }

        Path file = Paths.get(args[0]);
        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            System.out.println("Reading: " + line);
        }
    }
}
