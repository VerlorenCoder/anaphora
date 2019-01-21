package service;

import domain.english.Destination;
import domain.english.Number;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnglishNounsDatabase {

    private static Map<String, String> nounTypesByNounsDatabase;

    static {
        String csvFile = "/dictionary/english-nouns.csv";
        String separator = ",";
        String line;

        Map<String, String> nounTypesByNouns = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(EnglishNounsDatabase.class.getResource(csvFile).getFile()))) {
            System.out.println("Fetching all nouns from csv file " + csvFile);
            while ((line = br.readLine()) != null) {
                String[] record = line.split(separator);
                nounTypesByNouns.put(record[0], record[1]);
            }
            nounTypesByNounsDatabase = nounTypesByNouns;
            System.out.println("Done!");
        } catch (IOException e) {
            e.printStackTrace();
            nounTypesByNounsDatabase = new HashMap<>();
        }
    }


    private static boolean existsInDatabase(String noun) {
        return nounTypesByNounsDatabase.containsKey(noun);
    }

    public static List<Number> getPossibleNumbersOf(String noun) {

        if (existsInDatabase(noun)) {
            return Number.fromNounType(
                    nounTypesByNounsDatabase.get(noun));
        }
        else {
            return Arrays.asList(Number.values());
        }

    }

    public static List<Destination> getPossibleDestinationsOf(String noun) {
        if (existsInDatabase(noun)) {
            return Destination.fromNounType(
                    nounTypesByNounsDatabase.get(noun));
        }
        else {
            return Arrays.asList(Destination.values());
        }

    }
}
