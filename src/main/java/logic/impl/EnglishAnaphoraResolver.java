package logic.impl;

import domain.EnglishTag;
import domain.Sentence;
import logic.*;

import java.util.List;
import java.util.function.Consumer;

public class EnglishAnaphoraResolver implements AnaphoraResolver<EnglishTag> {
    private Splitter<EnglishTag> splitter;
    private Tokenizer tokenizer;
    private Tagger<EnglishTag> tagger;
    private PointArbitrator<EnglishTag> pointArbitrator;
    private AnaphoraFinder<EnglishTag> anaphoraFinder;
    private Consumer<List<Sentence<EnglishTag>>> afterSplit;
    private Consumer<List<Sentence<EnglishTag>>> afterTokenize;
    private Consumer<List<Sentence<EnglishTag>>> afterTag;
    private Consumer<List<Sentence<EnglishTag>>> afterPoints;

    private EnglishAnaphoraResolver(
            Splitter<EnglishTag> splitter,
            Tokenizer tokenizer,
            Tagger<EnglishTag> tagger,
            PointArbitrator<EnglishTag> pointArbitrator,
            AnaphoraFinder<EnglishTag> anaphoraFinder) {
        this.splitter = splitter;
        this.tokenizer = tokenizer;
        this.tagger = tagger;
        this.pointArbitrator = pointArbitrator;
        this.anaphoraFinder = anaphoraFinder;
    }

    public static Builder builder(
            Splitter<EnglishTag> splitter,
            Tokenizer tokenizer,
            Tagger<EnglishTag> tagger,
            PointArbitrator<EnglishTag> tokenArbitrator,
            AnaphoraFinder<EnglishTag> anaphoraFinder) {
        return new Builder(splitter, tokenizer, tagger, tokenArbitrator, anaphoraFinder);
    }

    public static class Builder {
        private Splitter<EnglishTag> splitter;
        private Tokenizer tokenizer;
        private Tagger<EnglishTag> tagger;
        private PointArbitrator<EnglishTag> pointArbitrator;
        private AnaphoraFinder<EnglishTag> anaphoraFinder;
        private Consumer<List<Sentence<EnglishTag>>> afterSplit;
        private Consumer<List<Sentence<EnglishTag>>> afterTokenize;
        private Consumer<List<Sentence<EnglishTag>>> afterTag;
        private Consumer<List<Sentence<EnglishTag>>> afterPoints;

        private Builder(
                Splitter<EnglishTag> splitter,
                Tokenizer tokenizer,
                Tagger<EnglishTag> tagger,
                PointArbitrator<EnglishTag> pointArbitrator,
                AnaphoraFinder<EnglishTag> anaphoraFinder) {
            this.splitter = splitter;
            this.tokenizer = tokenizer;
            this.tagger = tagger;
            this.pointArbitrator = pointArbitrator;
            this.anaphoraFinder = anaphoraFinder;
        }

        public Builder afterSplit(Consumer<List<Sentence<EnglishTag>>> afterSplit) {
            this.afterSplit = afterSplit;
            return this;
        }

        public Builder afterTokenize(Consumer<List<Sentence<EnglishTag>>> afterTokenize) {
            this.afterTokenize = afterTokenize;
            return this;
        }

        public Builder afterTag(Consumer<List<Sentence<EnglishTag>>> afterTag) {
            this.afterTag = afterTag;
            return this;
        }

        public Builder afterPoints(Consumer<List<Sentence<EnglishTag>>> afterPoints) {
            this.afterPoints = afterPoints;
            return this;
        }

        public EnglishAnaphoraResolver build() {
            EnglishAnaphoraResolver anaphoraResolver = new EnglishAnaphoraResolver(splitter, tokenizer, tagger, pointArbitrator, anaphoraFinder);
            anaphoraResolver.afterSplit = this.afterSplit != null ? this.afterSplit : o -> { };
            anaphoraResolver.afterTokenize = this.afterTokenize != null ? this.afterTokenize : o -> { };
            anaphoraResolver.afterTag = this.afterTag != null ? this.afterTag : o -> { };
            anaphoraResolver.afterPoints = this.afterPoints != null ? this.afterPoints : o -> { };
            return anaphoraResolver;
        }
    }

    @Override
    public List<Sentence<EnglishTag>> resolve(String textForAnalysis) {

        List<Sentence<EnglishTag>> sentences = splitter.split(textForAnalysis);
        notifyAfterSplit(sentences);

        tokenizer.tokenize(sentences);
        notifyAfterTokenize(sentences);

        tagger.tag(sentences);
        notifyAfterTag(sentences);

        pointArbitrator.admitPoints(sentences);
        notifyAfterPoints(sentences);

        anaphoraFinder.findAnaphora(sentences);
        return sentences;
    }

    private void notifyAfterSplit(List<Sentence<EnglishTag>> sentences) {
        afterSplit.accept(sentences);
    }
    private void notifyAfterTokenize(List<Sentence<EnglishTag>> sentences) {
        afterTokenize.accept(sentences);
    }
    private void notifyAfterTag(List<Sentence<EnglishTag>> sentences) {
        afterTag.accept(sentences);
    }
    private void notifyAfterPoints(List<Sentence<EnglishTag>> sentences) {
        afterPoints.accept(sentences);
    }

}
