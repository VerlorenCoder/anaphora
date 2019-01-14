package logic;

import domain.Sentence;

import java.util.List;

public interface Tokenizer {

    <T> void tokenize(List<Sentence<T>> sentences);
}
