package logic;

import domain.Token;
import java.util.List;

public interface Tagger {

    String[] tag(List<Token> tokens);
}
