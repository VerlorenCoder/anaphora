package logic;

import domain.english.Sentence;

import java.util.List;

public interface AnaphoraResolver<T> {

    List<Sentence<T>> resolve(String textForAnalysis);
}
