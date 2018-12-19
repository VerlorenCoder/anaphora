package domain;

public class Token {

    private String value;
    private String tag ; // ToDo: enum
    private Sentence sentence;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {

        return "[" + (sentence != null ? sentence.getIndex() : "?") + "] " + value + " :: " + tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
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
        private String tag;
        private Sentence sentence;

        private Builder() { }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder tag(String tag) {
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
