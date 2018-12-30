package logic;

import domain.Token;
import java.util.List;

public interface Tagger {

    void addTags(List<Token> tokens);
}
