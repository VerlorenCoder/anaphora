package logic;

import domain.Sentence;

import java.util.List;

public interface Tagger<T> {

    void tag(List<Sentence<T>> sentences);
}
