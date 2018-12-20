package logic.impl;

import domain.Sentence;
import domain.Token;
import logic.AnaphoraFinder;
import logic.Splitter;
import logic.Tagger;
import logic.Tokenizer;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnaphoraFinderImpl implements AnaphoraFinder {

    private Splitter splitter;
    private Tagger tagger;
    private Tokenizer tokenizer;
    private Consumer<List<Sentence>> afterSplit;
    private Consumer<List<Token>> afterTokenize;

    private AnaphoraFinderImpl(Splitter splitter, Tagger tagger, Tokenizer tokenizer) {
        this.splitter = splitter;
        this.tagger = tagger;
        this.tokenizer = tokenizer;
    }

    public static Builder builder(Splitter splitter, Tagger tagger, Tokenizer tokenizer) {
        return new Builder(splitter, tagger, tokenizer);
    }

    public static class Builder {

        private Splitter splitter;
        private Tagger tagger;
        private Tokenizer tokenizer;
        private Consumer<List<Sentence>> afterSplit;
        private Consumer<List<Token>> afterTokenize;

        private Builder(Splitter splitter, Tagger tagger, Tokenizer tokenizer) {
            this.splitter = splitter;
            this.tagger = tagger;
            this.tokenizer = tokenizer;
        }

        public Builder afterSplit(Consumer<List<Sentence>> afterSplit) {
            this.afterSplit = afterSplit;
            return this;
        }

        public Builder afterTokenize(Consumer<List<Token>> afterTokinize) {
            this.afterTokenize = afterTokinize;
            return this;
        }

        public AnaphoraFinderImpl build() {
            AnaphoraFinderImpl anaphoraFinder = new AnaphoraFinderImpl(splitter, tagger, tokenizer);
            anaphoraFinder.afterSplit = this.afterSplit != null ? this.afterSplit : o -> { };
            anaphoraFinder.afterTokenize = this.afterTokenize != null ? this.afterTokenize : o -> { };
            return anaphoraFinder;
        }
    }

    @Override
    public void analyze(String textForAnalysis) {

        // split
        List<Sentence> sentences = splitter.split(textForAnalysis);
        afterSplit.accept(sentences);

        // token
        List<Token> tokens = sentences
                .stream()
                .flatMap(sentence -> tokenizer.tokenize(sentence).stream())
                .collect(Collectors.toList());
        afterTokenize.accept(tokens);

        // tags
        String[] tags = tagger.tag(tokens);

        // Connecting tokens with tags
        for (int i = 0; i < tokens.size(); i++) {
            tokens.get(i).setTag(tags[i]);
        }
    }

}
