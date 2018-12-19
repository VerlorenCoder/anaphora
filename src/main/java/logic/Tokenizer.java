package logic;

import domain.Sentence;
import domain.Token;
import java.util.List;

public interface Tokenizer {

    List<Token> tokenize(Sentence sentence);
}
