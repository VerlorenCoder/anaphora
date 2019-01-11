package logic;

import domain.Sentence;

import java.util.List;

public interface AnaphoraResolver<T> {

    List<Sentence<T>> resolve(String textForAnalysis);
}
