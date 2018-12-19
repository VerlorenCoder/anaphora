package logic;

import domain.Sentence;
import domain.Token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tokenizer {

    public List<Token> tokenize(Sentence sentence) {

        String[] tokens = sentence.getContent().split(" ");

        return Arrays.stream(tokens)
                .map(token -> Token
                        .builder()
                        .value(token)
                        .sentence(sentence)
                        .build())
                .collect(Collectors.toList());
    }
}
