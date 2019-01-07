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
    private static int POINTS_FOR_ACCUSATIVE_EMPHASIS = 50;
    private static int POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS = 40;
    private static int POINTS_FOR_NON_ADVERBIAL_EMPHASIS = 40;
    private static int POINTS_FOR_HEAD_NOUN_EMPHASIS = 80;

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

        for (int currentTokenIndex = 0; currentTokenIndex < tokens.size(); currentTokenIndex++) {

            Token currentToken = tokens.get(currentTokenIndex);


            // biorę zdanie; szukam rzeczowników; punktuję rzeczowniki
            // 100 pkt na start
            if (isNoun(currentToken)) {
                addPointsForNoun(currentToken);
            }

            // obiekt istniejący nazwy własne - 70 pkt
            // tagi 14-15
            if (isProperNoun(currentToken)) {
                addPointsForProperNoun(currentToken);
            }

            // dopełnienie bliższe - 50 pkt
            // 3 + (7-9) + 12-15
            if (isAccusativeEmphasis(currentTokenIndex, tokens)) {
                addPointsForAccusativeEmphasis(currentToken);
            }

            // dopełnienie dalsze - 40 pkt
            // 19 + 12-15
            // "of" + (3) + (7-9) + 12-15
            if (isIndirectObjectAndObliqueComplementEmphasis(currentTokenIndex, tokens)) {
                addPointsForIndirectObjectAndObliqueComplementEmphasis(currentToken);
            }

            // nie przysłówek - 50 pkt
            // ! "of" + (3) + (7-9) + 12-15
            if (isNonAdverbialEmphasis(currentTokenIndex, tokens)) {
                addPointsForNonAdverbialEmphasis(currentToken);
            }

            // element głowny frazy - 80 pkt
            // GR
            if (isHeadNounEmphasis(currentTokenIndex, tokens)) {
                addPointForHeadNounEmphasis(currentToken);
            }
        }
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

    private boolean isAccusativeEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
        if (isNoun(analyzedToken)) {    // tag: 12-15
            while (hasPreviousToken(analyzedTokenIndex)) {
                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);
                if (isDeterminer(previousToken)) {          // tag: 3
                    return true;
                }
                else if (isNotAdjective(previousToken)){    // tag: !(7-9)
                    return false;
                }
                analyzedTokenIndex--;
            }
        }
        return false;
    }

    private boolean hasPreviousToken(int tokenIndex) {
        return tokenIndex > 0;
    }

    private Token getPreviousToken(int tokenIndex, List<Token> tokens) {
        return tokens.get(tokenIndex - 1);
    }

    private boolean isNotAdjective(Token token) {
        return !(token.getTag().equals(Tag.ADJECTIVE)
                || token.getTag().equals(Tag.ADJECTIVE_COMPARATIVE)
                || token.getTag().equals(Tag.ADJECTIVE_SUPERLATIVE));
    }

    private boolean isDeterminer(Token token) {
        return token.getTag().equals(Tag.DETERMINER);
    }

    private void addPointsForAccusativeEmphasis(Token token) {
        token.addPoints(POINTS_FOR_ACCUSATIVE_EMPHASIS);
    }

    private boolean isIndirectObjectAndObliqueComplementEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
        if (isNoun(analyzedToken)) {    // tag: 12-15

            // possibility #1 - tags: 19 + 12-15
            if (hasPreviousToken(analyzedTokenIndex)) {
                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);
                if (isPossessivePronoun(previousToken)) {
                    return true;
                }
            }

            // possibility #2 - tags: "of" + (3) + (7-9) + 12-15
            while (hasPreviousToken(analyzedTokenIndex)) {
                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);

                if (isOfWord(previousToken)) {  // word: "of"
                    return true;
                }
                else if (isNotAdjective(previousToken) && isNotDeterminer(previousToken)){    // tag: !(3, 7-9)
                    return false;
                }
                analyzedTokenIndex--;
            }
        }
        return false;
    }

    private boolean isPossessivePronoun(Token token) {
        return token.getTag().equals(Tag.POSSESSIVE_PRONOUN);
    }

    private boolean isNotDeterminer(Token token) {
        return !isDeterminer(token);
    }

    private boolean isOfWord(Token token) {
        return "of".equals(token.getValue());
    }

    private void addPointsForIndirectObjectAndObliqueComplementEmphasis(Token token) {
        token.addPoints(POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS);
    }

    // !("of" + (3) + (7-9)) + 12-15
    private boolean isNonAdverbialEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
        if (isNoun(analyzedToken)) {    // tag: 12-15

            while (hasPreviousToken(analyzedTokenIndex)) {
                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);

                if (isOfWord(previousToken)) {  // word: "of"
                    return false;
                }
                else if (isNotAdjective(previousToken) && isNotDeterminer(previousToken)){    // tag: !(3, 7-9)
                    return true;
                }
                analyzedTokenIndex--;
            }
            return true;
        }
        else {
            return false;
        }
    }

    private void addPointsForNonAdverbialEmphasis(Token token) {
        token.addPoints(POINTS_FOR_NON_ADVERBIAL_EMPHASIS);
    }

    private boolean isHeadNounEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
        // ToDo: Grześku, proszę to zaimplementować :)
        return false;
    }

    private void addPointForHeadNounEmphasis(Token token) {
        token.addPoints(POINTS_FOR_HEAD_NOUN_EMPHASIS);
    }

    private void notifyAfterPoints(Map<Sentence, List<Token>> sentencesWithTokens) {
        afterPoints.accept(sentencesWithTokens);
    }

}
