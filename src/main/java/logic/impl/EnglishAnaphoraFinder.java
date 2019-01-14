package logic.impl;

import domain.EnglishTag;
import domain.Sentence;
import domain.Token;
import logic.AnaphoraFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnglishAnaphoraFinder implements AnaphoraFinder<EnglishTag> {

    @Override
    public void findAnaphora(List<Sentence<EnglishTag>> sentences) {

        sentences.forEach(analyzedSentenceInWhichPronounsAreAssociatedWithNouns -> {
            List<Token<EnglishTag>> pronounsToAssociateWithNouns = getAllPronouns(analyzedSentenceInWhichPronounsAreAssociatedWithNouns);
            TokenWithCalculatedPoints nounOrPronounWithMaxPoints = getNounOrPronounWithMaxPoints(analyzedSentenceInWhichPronounsAreAssociatedWithNouns, sentences);

            pronounsToAssociateWithNouns.forEach(pronoun -> {
                if (isNoun(nounOrPronounWithMaxPoints.getToken())) {
                    pronoun.setRoot(nounOrPronounWithMaxPoints.getToken());
                    pronoun.setPoints(nounOrPronounWithMaxPoints.getToken().getPoints() + nounOrPronounWithMaxPoints.getCalculatedPoints());
                }
                else if (isPronoun(nounOrPronounWithMaxPoints.getToken()) && nounOrPronounWithMaxPoints.getToken().hasRoot()) {
                    pronoun.setRoot(nounOrPronounWithMaxPoints.getToken().getRoot());
                    pronoun.setPoints(nounOrPronounWithMaxPoints.getToken().getRoot().getPoints() + nounOrPronounWithMaxPoints.getCalculatedPoints());
                }
                else {
                    throw new IllegalStateException("nie powinno tak być :(");
                }
            });
        });
    }


    private TokenWithCalculatedPoints getNounOrPronounWithMaxPoints(Sentence<EnglishTag> analyzedSentence, List<Sentence<EnglishTag>> sentences) {
        List<Token<EnglishTag>> nounsAndPronounsToAnalyze = getAllNounsAndPronounsToAnalyze(analyzedSentence, sentences);
        int maxPoints = 0;
        Token<EnglishTag> nounOrPronounWithMaxPoints = null;

        for (Token<EnglishTag> currentNounOrPronoun : nounsAndPronounsToAnalyze) {
            int currentNounOrPronounPoints = currentNounOrPronoun.getPoints();
            switch (sentences.indexOf(analyzedSentence) - sentences.indexOf(currentNounOrPronoun.getSentence())) {
                case 0:
                    break;
                case 1:
                    currentNounOrPronounPoints /= 2;
                    break;
                case 2:
                    currentNounOrPronounPoints /= 4;
                    break;
                case 3:
                    currentNounOrPronounPoints /= 8;
                    break;
                default:
                    throw new IllegalStateException("nie powinno to się zdarzyć :O");
            }
            if (currentNounOrPronounPoints > maxPoints) {
                maxPoints = currentNounOrPronounPoints;
                nounOrPronounWithMaxPoints = currentNounOrPronoun;
            }
        }
        return new TokenWithCalculatedPoints(nounOrPronounWithMaxPoints, maxPoints);
    }

    private List<Token<EnglishTag>> getAllPronouns(Sentence<EnglishTag> sentence) {
        return sentence
                .getTokens()
                .stream()
                .filter(this::isPronoun)
                .collect(Collectors.toList());
    }

    private boolean isPronoun(Token<EnglishTag> token) {
        return EnglishTag.PERSONAL_PRONOUN.equals(token.getTag())
                || EnglishTag.POSSESSIVE_PRONOUN.equals(token.getTag())
                || EnglishTag.POSSESSIVE_WH_PRONOUN.equals(token.getTag())
                || EnglishTag.WH_PRONOUN.equals(token.getTag());
    }

    private List<Token<EnglishTag>> getAllNounsAndPronounsToAnalyze(Sentence<EnglishTag> sentence, List<Sentence<EnglishTag>> sentences) {

        // get all tokens from up to 4 previous sentences
        int sentenceIndex = sentences.indexOf(sentence);
        List<Token<EnglishTag>> nounsAndPronounsToReturn = new ArrayList<>();
        for (int i = sentenceIndex, j = 0; i >= 0 && j < 4; i--, j++) {
            nounsAndPronounsToReturn.addAll(sentences.get(i).getTokens());
        }
        // filter nouns and pronouns
        return nounsAndPronounsToReturn
                .stream()
                .filter(token -> isNoun(token) || isPronoun(token))
                .collect(Collectors.toList());
    }

    private boolean isNoun(Token<EnglishTag> token) {
        return token.getTag().equals(EnglishTag.NOUN_SINGULAR_OR_GROUP)
                || token.getTag().equals(EnglishTag.NOUN_PLURAL)
                || token.getTag().equals(EnglishTag.PROPER_NOUN_SINGULAR)
                || token.getTag().equals(EnglishTag.PROPER_NOUN_PLURAL);
    }



    private static class TokenWithCalculatedPoints {

        private Token<EnglishTag> token;
        private int calculatedPoints;

        private TokenWithCalculatedPoints(Token<EnglishTag> token, int calculatedPoints) {
            this.token = token;
            this.calculatedPoints = calculatedPoints;
        }

        public Token<EnglishTag> getToken() {
            return token;
        }

        public void setToken(Token<EnglishTag> token) {
            this.token = token;
        }

        public int getCalculatedPoints() {
            return calculatedPoints;
        }

        public void setCalculatedPoints(int calculatedPoints) {
            this.calculatedPoints = calculatedPoints;
        }
    }
}
