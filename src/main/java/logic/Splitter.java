package logic;

import domain.Sentence;

import java.util.List;

public interface Splitter<T> {

    List<Sentence<T>> split(String text);
}
