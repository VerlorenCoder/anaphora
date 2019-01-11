package logic.impl;

import domain.EnglishTag;
import domain.Sentence;
import logic.AnaphoraResolver;
import logic.Splitter;
import logic.Tagger;
import logic.Tokenizer;

import java.util.List;
import java.util.function.Consumer;

public class EnglishAnaphoraResolver implements AnaphoraResolver {

    private static int POINTS_FOR_NOUN = 100;
    private static int POINTS_FOR_SUBJECT = 80;
    private static int POINTS_FOR_PROPER_NOUN = 70;
    private static int POINTS_FOR_ACCUSATIVE_EMPHASIS = 50;
    private static int POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS = 40;
    private static int POINTS_FOR_NON_ADVERBIAL_EMPHASIS = 40;
    private static int POINTS_FOR_HEAD_NOUN_EMPHASIS = 80;

    private Splitter<EnglishTag> splitter;
    private Tokenizer tokenizer;
    private Tagger<EnglishTag> tagger;
    private Consumer<List<Sentence<EnglishTag>>> afterSplit;
    private Consumer<List<Sentence<EnglishTag>>> afterTokenize;
    private Consumer<List<Sentence<EnglishTag>>> afterTag;
    private Consumer<List<Sentence<EnglishTag>>> afterPoints;

    private EnglishAnaphoraResolver(Splitter<EnglishTag> splitter, Tokenizer tokenizer, Tagger<EnglishTag> tagger) {
        this.splitter = splitter;
        this.tokenizer = tokenizer;
        this.tagger = tagger;
    }

    public static Builder builder(Splitter<EnglishTag> splitter, Tokenizer tokenizer, Tagger<EnglishTag> tagger) {
        return new Builder(splitter, tokenizer, tagger);
    }

    public static class Builder {
        private Splitter<EnglishTag> splitter;
        private Tokenizer tokenizer;
        private Tagger<EnglishTag> tagger;
        private Consumer<List<Sentence<EnglishTag>>> afterSplit;
        private Consumer<List<Sentence<EnglishTag>>> afterTokenize;
        private Consumer<List<Sentence<EnglishTag>>> afterTag;
        private Consumer<List<Sentence<EnglishTag>>> afterPoints;

        private Builder(Splitter<EnglishTag> splitter, Tokenizer tokenizer, Tagger<EnglishTag> tagger) {
            this.splitter = splitter;
            this.tokenizer = tokenizer;
            this.tagger = tagger;
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
            EnglishAnaphoraResolver anaphoraFinder = new EnglishAnaphoraResolver(splitter, tokenizer, tagger);
            anaphoraFinder.afterSplit = this.afterSplit != null ? this.afterSplit : o -> { };
            anaphoraFinder.afterTokenize = this.afterTokenize != null ? this.afterTokenize : o -> { };
            anaphoraFinder.afterTag = this.afterTag != null ? this.afterTag : o -> { };
            anaphoraFinder.afterPoints = this.afterPoints != null ? this.afterPoints : o -> { };
            return anaphoraFinder;
        }
    }

    @Override
    public void analyze(String textForAnalysis) {

        List<Sentence<EnglishTag>> sentences = splitter.split(textForAnalysis);
        notifyAfterSplit(sentences);

        tokenizer.tokenize(sentences);
        notifyAfterTokenize(sentences);

        tagger.tag(sentences);
        notifyAfterTag(sentences);

        // nouns - points
//        addPoints(sentencesWithTokens);
//        notifyAfterPoints(sentencesWithTokens);
//
//        // pronouns
//        associatePronounsWithNouns(rawSentences, sentencesWithTokens);
//
//        System.out.println();
//        // ToDo: ramka na 4 zdania itd.
//
//        // [kryterium zgodności rodzaju]
//
//        // Biorę pierwsze zdanie
//        // punktuję tagi
//
//        // Punktuję drugie zdanie

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




//    private void addPoints(Map<Sentence, List<Token>> sentencesWithTokens) {
//        sentencesWithTokens.forEach(this::setPointsInEachSentence);
//    }
//
//    private void setPointsInEachSentence(Sentence sentence, List<Token> tokens) {
//
//        // podmiot - pierwszy rzeczownik w zdaniu - 80 pkt
//        // tagi 12-15
//        Optional<Token> subject = getSubject(tokens);
//        subject.ifPresent(this::addPointsForSubject);
//
//        for (int currentTokenIndex = 0; currentTokenIndex < tokens.size(); currentTokenIndex++) {
//
//            Token currentToken = tokens.get(currentTokenIndex);
//
//
//            // biorę zdanie; szukam rzeczowników; punktuję rzeczowniki
//            // 100 pkt na start
//            if (isNoun(currentToken)) {
//                addPointsForNoun(currentToken);
//            }
//
//            // obiekt istniejący nazwy własne - 70 pkt
//            // tagi 14-15
//            if (isProperNoun(currentToken)) {
//                addPointsForProperNoun(currentToken);
//            }
//
//            // dopełnienie bliższe - 50 pkt
//            // 3 + (7-9) + 12-15
//            if (isAccusativeEmphasis(currentTokenIndex, tokens)) {
//                addPointsForAccusativeEmphasis(currentToken);
//            }
//
//            // dopełnienie dalsze - 40 pkt
//            // 19 + 12-15
//            // "of" + (3) + (7-9) + 12-15
//            if (isIndirectObjectAndObliqueComplementEmphasis(currentTokenIndex, tokens)) {
//                addPointsForIndirectObjectAndObliqueComplementEmphasis(currentToken);
//            }
//
//            // nie przysłówek - 50 pkt
//            // ! "of" + (3) + (7-9) + 12-15
//            if (isNonAdverbialEmphasis(currentTokenIndex, tokens)) {
//                addPointsForNonAdverbialEmphasis(currentToken);
//            }
//
//            // element głowny frazy - 80 pkt
//            // GR
//            if (isHeadNounEmphasis(currentTokenIndex, tokens)) {
//                addPointForHeadNounEmphasis(currentToken);
//            }
//        }
//    }
//
//    private Optional<Token> getSubject(List<Token> tokens) {
//        return tokens.stream().filter(this::isNoun).findFirst();
//    }
//
//    private void addPointsForSubject(Token token) {
//        token.addPoints(POINTS_FOR_SUBJECT);
//    }
//
//    private boolean isNoun(Token token) {
//        return token.getTag().equals(EnglishTag.NOUN_SINGULAR_OR_GROUP)
//                || token.getTag().equals(EnglishTag.NOUN_PLURAL)
//                || token.getTag().equals(EnglishTag.PROPER_NOUN_SINGULAR)
//                || token.getTag().equals(EnglishTag.PROPER_NOUN_PLURAL);
//    }
//
//    private void addPointsForNoun(Token token) {
//        token.addPoints(POINTS_FOR_NOUN);
//    }
//
//    private boolean isProperNoun(Token token) {
//        return token.getTag().equals(EnglishTag.PROPER_NOUN_SINGULAR)
//                || token.getTag().equals(EnglishTag.PROPER_NOUN_PLURAL);
//    }
//
//    private void addPointsForProperNoun(Token token) {
//        token.addPoints(POINTS_FOR_PROPER_NOUN);
//    }
//
//    private boolean isAccusativeEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
//        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
//        if (isNoun(analyzedToken)) {    // tag: 12-15
//            while (hasPreviousToken(analyzedTokenIndex)) {
//                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);
//                if (isDeterminer(previousToken)) {          // tag: 3
//                    return true;
//                }
//                else if (isNotAdjective(previousToken)){    // tag: !(7-9)
//                    return false;
//                }
//                analyzedTokenIndex--;
//            }
//        }
//        return false;
//    }
//
//    private boolean hasPreviousToken(int tokenIndex) {
//        return tokenIndex > 0;
//    }
//
//    private Token getPreviousToken(int tokenIndex, List<Token> tokens) {
//        return tokens.get(tokenIndex - 1);
//    }
//
//    private boolean isNotAdjective(Token token) {
//        return !(token.getTag().equals(EnglishTag.ADJECTIVE)
//                || token.getTag().equals(EnglishTag.ADJECTIVE_COMPARATIVE)
//                || token.getTag().equals(EnglishTag.ADJECTIVE_SUPERLATIVE));
//    }
//
//    private boolean isDeterminer(Token token) {
//        return token.getTag().equals(EnglishTag.DETERMINER);
//    }
//
//    private void addPointsForAccusativeEmphasis(Token token) {
//        token.addPoints(POINTS_FOR_ACCUSATIVE_EMPHASIS);
//    }
//
//    private boolean isIndirectObjectAndObliqueComplementEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
//        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
//        if (isNoun(analyzedToken)) {    // tag: 12-15
//
//            // possibility #1 - tags: 19 + 12-15
//            if (hasPreviousToken(analyzedTokenIndex)) {
//                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);
//                if (isPossessivePronoun(previousToken)) {
//                    return true;
//                }
//            }
//
//            // possibility #2 - tags: "of" + (3) + (7-9) + 12-15
//            while (hasPreviousToken(analyzedTokenIndex)) {
//                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);
//
//                if (isOfWord(previousToken)) {  // word: "of"
//                    return true;
//                }
//                else if (isNotAdjective(previousToken) && isNotDeterminer(previousToken)){    // tag: !(3, 7-9)
//                    return false;
//                }
//                analyzedTokenIndex--;
//            }
//        }
//        return false;
//    }
//
//    private boolean isPossessivePronoun(Token token) {
//        return token.getTag().equals(EnglishTag.POSSESSIVE_PRONOUN);
//    }
//
//    private boolean isNotDeterminer(Token token) {
//        return !isDeterminer(token);
//    }
//
//    private boolean isOfWord(Token token) {
//        return "of".equals(token.getValue());
//    }
//
//    private void addPointsForIndirectObjectAndObliqueComplementEmphasis(Token token) {
//        token.addPoints(POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS);
//    }
//
//    // !("of" + (3) + (7-9)) + 12-15
//    private boolean isNonAdverbialEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
//        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
//        if (isNoun(analyzedToken)) {    // tag: 12-15
//
//            while (hasPreviousToken(analyzedTokenIndex)) {
//                Token previousToken = getPreviousToken(analyzedTokenIndex, sentenceTokens);
//
//                if (isOfWord(previousToken)) {  // word: "of"
//                    return false;
//                }
//                else if (isNotAdjective(previousToken) && isNotDeterminer(previousToken)){    // tag: !(3, 7-9)
//                    return true;
//                }
//                analyzedTokenIndex--;
//            }
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    private void addPointsForNonAdverbialEmphasis(Token token) {
//        token.addPoints(POINTS_FOR_NON_ADVERBIAL_EMPHASIS);
//    }
//
//    private boolean isHeadNounEmphasis(int analyzedTokenIndex, List<Token> sentenceTokens) {
//        Token analyzedToken = sentenceTokens.get(analyzedTokenIndex);
//        // ToDo: Grześku, proszę to zaimplementować :)
//        return false;
//    }
//
//    private void addPointForHeadNounEmphasis(Token token) {
//        token.addPoints(POINTS_FOR_HEAD_NOUN_EMPHASIS);
//    }
//
//    private void notifyAfterPoints(Map<Sentence, List<Token>> sentencesWithTokens) {
//        afterPoints.accept(sentencesWithTokens);
//    }
//
//    private void associatePronounsWithNouns(List<Sentence> sentences, Map<Sentence, List<Token>> sentencesWithTokens) {
//
//        sentences.forEach(analyzedSentenceInWhichPronounsAreAssociatedWithNouns -> {
//            int analyzedSentenceIndex = analyzedSentenceInWhichPronounsAreAssociatedWithNouns.getIndex();
//            List<Token> analyzedSentenceTokens = sentencesWithTokens.get(analyzedSentenceInWhichPronounsAreAssociatedWithNouns);
//            List<Token> pronounsToAssociateWithNouns = getAllPronouns(analyzedSentenceTokens);
//            List<Token> nounsAndPronouns = getAllNounsAndPronounsToAnalyze(analyzedSentenceInWhichPronounsAreAssociatedWithNouns, sentences, sentencesWithTokens);
//            Pair<Token, Integer> nounOrPronounWithMaxPoints = getNounOrPronounWithMaxPoints(nounsAndPronouns, analyzedSentenceIndex);
//
//            pronounsToAssociateWithNouns.forEach(pronoun -> {
//                if (isNoun(nounOrPronounWithMaxPoints.getKey())) {
//                    pronoun.setRoot(nounOrPronounWithMaxPoints.getKey());
//                    pronoun.setPoints(nounOrPronounWithMaxPoints.getKey().getPoints() + nounOrPronounWithMaxPoints.getValue());
//                }
//                else if (isPronoun(nounOrPronounWithMaxPoints.getKey()) && nounOrPronounWithMaxPoints.getKey().hasRoot()) {
//                    pronoun.setRoot(nounOrPronounWithMaxPoints.getKey().getRoot());
//                    pronoun.setPoints(nounOrPronounWithMaxPoints.getKey().getRoot().getPoints() + nounOrPronounWithMaxPoints.getValue());
//                }
//                else {
//                    throw new IllegalStateException("nie powinno tak być :(");
//                }
//            });
//        });
//    }
//
//    private Pair<Token, Integer> getNounOrPronounWithMaxPoints(List<Token> nounsAndPronouns, int analyzedSentenceIndex) {
//
//        int maxPoints = 0;
//        Token nounOrPronounWithMaxPoints = null;
//
//        for (Token currentNounOrPronoun : nounsAndPronouns) {
//            int currentNounOrPronounPoints = currentNounOrPronoun.getPoints();
//            switch (analyzedSentenceIndex - currentNounOrPronoun.getSentence().getIndex()) {
//                case 0:
//                    break;
//                case 1:
//                    currentNounOrPronounPoints /= 2;
//                    break;
//                case 2:
//                    currentNounOrPronounPoints /= 4;
//                    break;
//                case 3:
//                    currentNounOrPronounPoints /= 8;
//                    break;
//                default:
//                    throw new IllegalStateException("nie powinno to się zdarzyć :O");
//            }
//            if (currentNounOrPronounPoints > maxPoints) {
//                maxPoints = currentNounOrPronounPoints;
//                nounOrPronounWithMaxPoints = currentNounOrPronoun;
//            }
//        }
//        return new Pair<>(nounOrPronounWithMaxPoints, maxPoints);
//    }
//
//    private List<Token> getAllPronouns(List<Token> tokens) {
//        return tokens
//                .stream()
//                .filter(this::isPronoun)
//                .collect(Collectors.toList());
//    }
//
//    private boolean isPronoun(Token token) {
//        return EnglishTag.PERSONAL_PRONOUN.equals(token.getTag())
//                || EnglishTag.POSSESSIVE_PRONOUN.equals(token.getTag())
//                || EnglishTag.POSSESSIVE_WH_PRONOUN.equals(token.getTag())
//                || EnglishTag.WH_PRONOUN.equals(token.getTag());
//    }
//
//    private List<Token> getAllNounsAndPronounsToAnalyze(Sentence sentence, List<Sentence> sentences, Map<Sentence, List<Token>> sentencesWithTokens) {
//        int sentenceIndex = sentences.indexOf(sentence);
//        List<Token> nounsAndPronounsToReturn = new ArrayList<>();
//        // get tokens
//        for (int i = sentenceIndex, j = 0; i >= 0 && j < 4; i--, j++) {
//            nounsAndPronounsToReturn.addAll(sentencesWithTokens.get(sentences.get(i)));
//        }
//        // filter nouns and pronouns
//        return nounsAndPronounsToReturn
//                .stream()
//                .filter(token -> isNoun(token) || isPronoun(token))
//                .collect(Collectors.toList());
//    }


}
