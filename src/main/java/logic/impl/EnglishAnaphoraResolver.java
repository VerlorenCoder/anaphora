package logic.impl;

import domain.EnglishTag;
import domain.Sentence;
import logic.*;

import java.util.List;
import java.util.function.Consumer;

public class EnglishAnaphoraResolver implements AnaphoraResolver {
    private Splitter<EnglishTag> splitter;
    private Tokenizer tokenizer;
    private Tagger<EnglishTag> tagger;
    private PointArbitrator<EnglishTag> pointArbitrator;
    private Consumer<List<Sentence<EnglishTag>>> afterSplit;
    private Consumer<List<Sentence<EnglishTag>>> afterTokenize;
    private Consumer<List<Sentence<EnglishTag>>> afterTag;
    private Consumer<List<Sentence<EnglishTag>>> afterPoints;

    private EnglishAnaphoraResolver(
            Splitter<EnglishTag> splitter,
            Tokenizer tokenizer,
            Tagger<EnglishTag> tagger,
            PointArbitrator<EnglishTag> pointArbitrator) {
        this.splitter = splitter;
        this.tokenizer = tokenizer;
        this.tagger = tagger;
        this.pointArbitrator = pointArbitrator;
    }

    public static Builder builder(
            Splitter<EnglishTag> splitter,
            Tokenizer tokenizer,
            Tagger<EnglishTag> tagger,
            PointArbitrator<EnglishTag> tokenArbitrator) {
        return new Builder(splitter, tokenizer, tagger, tokenArbitrator);
    }

    public static class Builder {
        private Splitter<EnglishTag> splitter;
        private Tokenizer tokenizer;
        private Tagger<EnglishTag> tagger;
        private PointArbitrator<EnglishTag> pointArbitrator;
        private Consumer<List<Sentence<EnglishTag>>> afterSplit;
        private Consumer<List<Sentence<EnglishTag>>> afterTokenize;
        private Consumer<List<Sentence<EnglishTag>>> afterTag;
        private Consumer<List<Sentence<EnglishTag>>> afterPoints;

        private Builder(
                Splitter<EnglishTag> splitter,
                Tokenizer tokenizer,
                Tagger<EnglishTag> tagger,
                PointArbitrator<EnglishTag> pointArbitrator) {
            this.splitter = splitter;
            this.tokenizer = tokenizer;
            this.tagger = tagger;
            this.pointArbitrator = pointArbitrator;
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
            EnglishAnaphoraResolver anaphoraFinder = new EnglishAnaphoraResolver(splitter, tokenizer, tagger, pointArbitrator);
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

        pointArbitrator.admitPoints(sentences);
        notifyAfterPoints(sentences);
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
    private void notifyAfterPoints(List<Sentence<EnglishTag>> sentences) {
        afterPoints.accept(sentences);
    }


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
