package logic;

import domain.english.Sentence;

import java.util.List;

public interface PointArbitrator<T> {

    void admitPoints(List<Sentence<T>> sentences);
}
