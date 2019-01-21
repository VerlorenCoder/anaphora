package logic;

import domain.english.Sentence;

import java.util.List;

public interface AnaphoraFinder<T> {

    void findAnaphora(List<Sentence<T>> sentences);
}
