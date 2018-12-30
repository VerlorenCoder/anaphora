package logic.impl;

import domain.Sentence;
import domain.Tag;
import domain.Token;
import logic.AnaphoraResolver;
import logic.Splitter;
import logic.Tagger;
import logic.Tokenizer;

import java.util.*;
import java.util.function.Consumer;

public class AnaphoraResolverImpl implements AnaphoraResolver {

    private static int POINTS_FOR_NOUN = 100;
    private static int POINTS_FOR_SUBJECT = 80;
    private static int POINTS_FOR_PROPER_NOUN = 70;

    private Splitter splitter;
    private Tagger tagger;
    private Tokenizer tokenizer;
    private Consumer<List<Sentence>> afterSplit;
    private Consumer<Map<Sentence, List<Token>>> afterTokenize;
    private Consumer<Map<Sentence, List<Token>>> afterTag;
    private Consumer<Map<Sentence, List<Token>>> afterPoints;


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
        private Consumer<Map<Sentence, List<Token>>> afterTokenize;
        private Consumer<Map<Sentence, List<Token>>> afterTag;
        private Consumer<Map<Sentence, List<Token>>> afterPoints;

        private Builder(Splitter splitter, Tagger tagger, Tokenizer tokenizer) {
            this.splitter = splitter;
            this.tagger = tagger;
            this.tokenizer = tokenizer;
        }

        public Builder afterSplit(Consumer<List<Sentence>> afterSplit) {
            this.afterSplit = afterSplit;
            return this;
        }

        public Builder afterTokenize(Consumer<Map<Sentence, List<Token>>> afterTokenize) {
            this.afterTokenize = afterTokenize;
            return this;
        }

        public Builder afterTag(Consumer<Map<Sentence, List<Token>>> afterTag) {
            this.afterTag = afterTag;
            return this;
        }

        public Builder afterPoints(Consumer<Map<Sentence, List<Token>>> afterPoints) {
            this.afterPoints = afterPoints;
            return this;
        }

        public AnaphoraResolverImpl build() {
            AnaphoraResolverImpl anaphoraFinder = new AnaphoraResolverImpl(splitter, tagger, tokenizer);
            anaphoraFinder.afterSplit = this.afterSplit != null ? this.afterSplit : o -> { };
            anaphoraFinder.afterTokenize = this.afterTokenize != null ? this.afterTokenize : o -> { };
            anaphoraFinder.afterTag = this.afterTag != null ? this.afterTag : o -> { };
            anaphoraFinder.afterPoints = this.afterPoints != null ? this.afterPoints : o -> { };
            return anaphoraFinder;
        }
    }

    @Override
    public void analyze(String textForAnalysis) {

        // split
        List<Sentence> sentences = splitToSentences(textForAnalysis);
        notifyAfterSplit(sentences);

        // tokenize
        Map<Sentence, List<Token>> sentencesWithTokens = splitToTokens(sentences);
        notifyAfterTokenize(sentencesWithTokens);

        // tag
        // ToDo: Dwie lub więcej nazw własnych pod rząd to skleić [tagi 14-15] - zrobione w taggerze //J.
        addTags(sentencesWithTokens);
        notifyAfterTag(sentencesWithTokens);

        // points
        addPoints(sentencesWithTokens);
        notifyAfterPoints(sentencesWithTokens);

        // ToDo: ramka na 4 zdania itd.

    }

    private List<Sentence> splitToSentences(String textForAnalysis) {
        return splitter.split(textForAnalysis);
    }

    private void notifyAfterSplit(List<Sentence> sentences) {
        afterSplit.accept(sentences);
    }


    private Map<Sentence, List<Token>> splitToTokens(List<Sentence> sentences) {

        Map<Sentence, List<Token>> sentencesWithTokens = new LinkedHashMap<>();

        sentences.forEach(sentence -> {
            List<Token> tokens = tokenizer.tokenize(sentence);
            sentencesWithTokens.put(sentence, tokens);
        });

        return sentencesWithTokens;
    }

    private void notifyAfterTokenize(Map<Sentence, List<Token>> tokens) {
        afterTokenize.accept(tokens);
    }


    private void addTags(Map<Sentence, List<Token>> sentencesWithTokens) {
        sentencesWithTokens.forEach((sentence, tokens) -> tagger.addTags(tokens));
    }

    private void notifyAfterTag(Map<Sentence, List<Token>> tokens) {
        afterTag.accept(tokens);
    }


    private void addPoints(Map<Sentence, List<Token>> sentencesWithTokens) {
        sentencesWithTokens.forEach(this::setPointsInEachSentence);
    }

    private void setPointsInEachSentence(Sentence sentence, List<Token> tokens) {

        // podmiot - pierwszy rzeczownik w zdaniu - 80 pkt
        // tagi 12-15
        Optional<Token> subject = getSubject(tokens);
        subject.ifPresent(this::addPointsForSubject);

        tokens.forEach(token -> {

            // biorę zdanie; szukam rzeczowników; punktuję rzeczowniki
            // 100 pkt na start
            if (isNoun(token)) {
                addPointsForNoun(token);
            }

            // obiekt istniejący nazwy własne - 70 pkt
            // tagi 14-15
            if (isProperNoun(token)) {
                addPointsForProperNoun(token);
            }

        });


        // dopełnienie bliższe - 50 pkt
        // 3 + (7-9) + 12-15

        // dopełnienie dalsze - 40 pkt
        // 19 + 12-15
        // "of" + (3) + (7-9) + 12-15

        // nie przysłówek - 50 pkt
        // ! "of" + (3) + (7-9) + 12-15

        // element głowny frazy - 80 pkt
        // GR
    }

    private Optional<Token> getSubject(List<Token> tokens) {
        return tokens.stream().filter(this::isNoun).findFirst();
    }

    private void addPointsForSubject(Token token) {
        token.addPoints(POINTS_FOR_SUBJECT);
    }

    private boolean isNoun(Token token) {
        return token.getTag().equals(Tag.NOUN_SINGULAR_OR_GROUP)
                || token.getTag().equals(Tag.NOUN_PLURAL)
                || token.getTag().equals(Tag.PROPER_NOUN_SINGULAR)
                || token.getTag().equals(Tag.PROPER_NOUN_PLURAL);
    }

    private void addPointsForNoun(Token token) {
        token.addPoints(POINTS_FOR_NOUN);
    }

    private boolean isProperNoun(Token token) {
        return token.getTag().equals(Tag.PROPER_NOUN_SINGULAR)
                || token.getTag().equals(Tag.PROPER_NOUN_PLURAL);
    }

    private void addPointsForProperNoun(Token token) {
        token.addPoints(POINTS_FOR_PROPER_NOUN);
    }



    private void notifyAfterPoints(Map<Sentence, List<Token>> sentencesWithTokens) {
        afterPoints.accept(sentencesWithTokens);
    }

}
