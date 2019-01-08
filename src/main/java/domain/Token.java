package domain;

public class Token {

    private String value;
    private Tag tag ;
    private Sentence sentence;
    private int points = 0;
    private Token root;

    public static Builder builder() {
        return new Builder();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Sentence getSentence() {

        return sentence;
    }

    public void setSentence(Sentence sentence) {

        this.sentence = sentence;
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

    public Token getRoot() {
        return root;
    }

    public void setRoot(Token root) {
        this.root = root;
    }

    public boolean hasRoot() {
        return root != null;
    }

    public static class Builder {

        private String value;
        private Tag tag;
        private Sentence sentence;

        private Builder() { }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder tag(Tag tag) {
            this.tag = tag;
            return this;
        }

        public Builder sentence(Sentence sentence) {
            this.sentence = sentence;
            return this;
        }

        public Token build() {
            Token token = new Token();
            token.setTag(tag);
            token.setValue(value);
            token.setSentence(sentence);
            return token;
        }
    }
}
