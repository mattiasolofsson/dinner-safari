package com.mocab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by mattias on 2015-04-20.
 */
@SpringBootApplication
public class DinnerSafariApplication implements CommandLineRunner {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\t");

    @Autowired
    private PartyGenerator generator;

    public static void main(String[] args) {
        SpringApplication.run(DinnerSafariApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        List<Couple> couples = this.createCouples();
        System.out.println();
        System.out.println("=== DELTAGARE ==========================");
        for (Couple couple : couples) {
            System.out.println(couple.getName() + ", " + couple.getAddress());
        }
        generator.doTheDance(couples);
    }

    private List<Couple> createCouples() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("couples.tsv"), StandardCharsets.UTF_8);
        List<Couple> couples = new ArrayList<>();
        reader.lines().map(SPLIT_PATTERN::splitAsStream).forEach(line -> couples.add(new Couple(line.collect(Collectors.toList()))));
        reader.close();
        return couples;
    }
}
