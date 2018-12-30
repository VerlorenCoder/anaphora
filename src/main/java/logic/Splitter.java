package logic;

import domain.Sentence;
import java.util.List;

public interface Splitter {

    List<Sentence> split(String sentence);
}
