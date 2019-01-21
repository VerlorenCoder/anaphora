package logic.impl;

import java.util.ArrayList;
import java.util.List;

public class PolishSplitter {
    private static final String REGEX = "\\.";

    public static List<String> split(String text) {
        List<String> result = new ArrayList<>();
        String[] sentences = text.split(REGEX);

        for(String sentence : sentences) {
            if(sentence.replace(" ", "").isEmpty()) {
                continue;
            }

            sentence = sentence.trim();
            sentence += ".";
            result.add(sentence);
        }

        return result;
    }
}
