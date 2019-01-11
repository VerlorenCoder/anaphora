package domain;

import java.util.ArrayList;
import java.util.List;

public final class Sentence<T> {

    private String value;
    private List<Token<T>> tokens = new ArrayList<>();


    public Sentence(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Token<T>> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token<T>> tokens) {
        this.tokens = tokens;
    }
}
