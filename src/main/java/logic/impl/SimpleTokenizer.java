package logic.impl;

import domain.Sentence;
import domain.Token;
import logic.Tokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleTokenizer implements Tokenizer {

    private final static String SPLIT_REGEX = " ";

    @Override
    public List<Token> tokenize(Sentence sentence) {

        String[] tokens = sentence.getContent().split(SPLIT_REGEX);

        return Arrays.stream(tokens)
                .map(token -> Token
                        .builder()
                        .value(token)
                        .sentence(sentence)
                        .build())
                .collect(Collectors.toList());
    }
}
