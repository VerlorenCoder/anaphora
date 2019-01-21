package domain.polish;

import java.util.ArrayList;
import java.util.List;

public class PolishSentence {
    private String value;
    private List<PolishToken> tokens = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<PolishToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<PolishToken> tokens) {
        this.tokens = tokens;
    }
}
