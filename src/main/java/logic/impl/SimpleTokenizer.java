package logic.impl;

import domain.Sentence;
import domain.Token;
import logic.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class SimpleTokenizer implements Tokenizer {

    private final static String SPLIT_REGEX = " ";

    @Override
    public <T> void tokenize(List<Sentence<T>> sentences) {

        for (Sentence<T> sentence : sentences) {
            String[] rawTokens = splitIntoRawTokens(sentence);

            List<Token<T>> tokens = new ArrayList<>();
            for (String rawToken : rawTokens) {
                Token<T> token = new Token<>(rawToken, sentence);
                tokens.add(token);
            }
            sentence.setTokens(tokens);
        }
    }

    private String[] splitIntoRawTokens(Sentence<?> sentence) {
        return sentence.getValue().split(SPLIT_REGEX);
    }
}
