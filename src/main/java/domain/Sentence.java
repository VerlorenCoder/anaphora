package domain;

public class Sentence {

    private String content;
    private int index;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return content;
    }

    public String toStringWithIndexes() {
        return "[" + index + "] " + content;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static class Builder {

        private String content;
        private int index;

        private Builder() { }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder index(int index) {
            this.index = index;
            return this;
        }

        public Sentence build() {
            Sentence sentence = new Sentence();
            sentence.setIndex(index);
            sentence.setContent(content);
            return sentence;
        }
    }
}
