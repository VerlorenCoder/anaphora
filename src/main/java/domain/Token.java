package domain;

public class Token {

    private String value;
    private Tag tag ;
    private Sentence sentence;

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
