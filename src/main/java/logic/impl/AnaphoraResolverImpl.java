package logic.impl;

import domain.Sentence;
import domain.Token;
import logic.AnaphoraResolver;
import logic.Splitter;
import logic.Tagger;
import logic.Tokenizer;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnaphoraResolverImpl implements AnaphoraResolver {

    private Splitter splitter;
    private Tagger tagger;
    private Tokenizer tokenizer;
    private Consumer<List<Sentence>> afterSplit;
    private Consumer<List<Token>> afterTokenize;
    private Consumer<List<Token>> afterTag;

    private AnaphoraResolverImpl(Splitter splitter, Tagger tagger, Tokenizer tokenizer) {
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
        private Consumer<List<Token>> afterTag;

        private Builder(Splitter splitter, Tagger tagger, Tokenizer tokenizer) {
            this.splitter = splitter;
            this.tagger = tagger;
            this.tokenizer = tokenizer;
        }

        public Builder afterSplit(Consumer<List<Sentence>> afterSplit) {
            this.afterSplit = afterSplit;
            return this;
        }

        public Builder afterTokenize(Consumer<List<Token>> afterTokenize) {
            this.afterTokenize = afterTokenize;
            return this;
        }

        public Builder afterTag(Consumer<List<Token>> afterTag) {
            this.afterTag = afterTag;
            return this;
        }

        public AnaphoraResolverImpl build() {
            AnaphoraResolverImpl anaphoraFinder = new AnaphoraResolverImpl(splitter, tagger, tokenizer);
            anaphoraFinder.afterSplit = this.afterSplit != null ? this.afterSplit : o -> { };
            anaphoraFinder.afterTokenize = this.afterTokenize != null ? this.afterTokenize : o -> { };
            anaphoraFinder.afterTag = this.afterTag != null ? this.afterTag : o -> { };
            return anaphoraFinder;
        }
    }

    @Override
    public void analyze(String textForAnalysis) {

        // split
        List<Sentence> sentences = splitToSentences(textForAnalysis);
        notifyAfterSplit(sentences);

        // tokenize
        List<Token> tokens = splitToTokens(sentences);
        notifyAfterTokenize(tokens);

        // tag
        addTags(tokens);
        notifyAfterTag(tokens);

    }

    private List<Token> splitToTokens(List<Sentence> sentences) {

        return sentences
                .stream()
                .flatMap(sentence -> tokenizer.tokenize(sentence).stream())
                .collect(Collectors.toList());
    }

    private void notifyAfterSplit(List<Sentence> sentences) {
        afterSplit.accept(sentences);
    }

    private List<Sentence> splitToSentences(String textForAnalysis) {
        return splitter.split(textForAnalysis);
    }

    private void notifyAfterTokenize(List<Token> tokens) {
        afterTokenize.accept(tokens);
    }

    private void addTags(List<Token> tokens) {
        tagger.addTags(tokens);
    }

    private void notifyAfterTag(List<Token> tokens) {
        afterTag.accept(tokens);
    }

}
