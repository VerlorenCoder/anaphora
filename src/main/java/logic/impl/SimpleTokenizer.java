package logic.impl;

import domain.Sentence;
import domain.Token;
import logic.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class SimpleTokenizer implements Tokenizer {

    private final static String SPLIT_REGEX = " ";
    private final static String PURGE_REGEX = "[.?!;:\\-{}\\[\\]()'\"]";
    private final static String EMPTY = " ";


    @Override
    public <T> void tokenize(List<Sentence<T>> sentences) {

        for (Sentence<T> sentence : sentences) {
            String[] rawTokens = splitIntoRawTokens(sentence);
            String[] purgedRawTokens = purgePunctuation(rawTokens);

            List<Token<T>> tokens = new ArrayList<>();
            for (String purgedRawToken : purgedRawTokens) {
                Token<T> token = new Token<>(purgedRawToken, sentence);
                tokens.add(token);
            }
            sentence.setTokens(tokens);
        }
    }

    private String[] splitIntoRawTokens(Sentence<?> sentence) {
        return sentence.getValue().split(SPLIT_REGEX);
    }

    private String[] purgePunctuation(String[] rawTokens) {
        String[] purgedRawTokens = new String[rawTokens.length];
        for (int i = 0; i < rawTokens.length; i++) {
            purgedRawTokens[i] = purgePunctuation(rawTokens[i]);
        }
        return purgedRawTokens;
    }

    private String purgePunctuation(String rawToken) {
        return rawToken.replaceAll(PURGE_REGEX, EMPTY).trim();
    }
}
