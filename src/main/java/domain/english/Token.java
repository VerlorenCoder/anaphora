package domain.english;

public final class Token<T> {

    private String value;
    private Sentence<T> sentence;
    private T tag ;
    private int points = 0;
    private Token<T> root;

    public Token(String value, Sentence<T> sentence) {
        this.value = value;
        this.sentence = sentence;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Sentence<T> getSentence() {
        return sentence;
    }

    public void setSentence(Sentence<T> sentence) {
        this.sentence = sentence;
    }

    public T getTag() {
        return tag;
    }

    public void setTag(T tag) {
        this.tag = tag;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public Token<T> getRoot() {
        return root;
    }

    public void setRoot(Token<T> root) {
        this.root = root;
    }

    public boolean hasRoot() {
        return this.root != null;
    }
}
