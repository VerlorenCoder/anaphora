package logic.impl;

import domain.english.Sentence;
import domain.english.Token;
import domain.english.Destination;
import domain.english.EnglishPronoun;
import domain.english.EnglishTag;
import domain.english.Number;
import logic.AnaphoraFinder;
import service.EnglishNounsDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnglishAnaphoraFinder implements AnaphoraFinder<EnglishTag> {

    @Override
    public void findAnaphora(List<Sentence<EnglishTag>> sentences) {

        sentences.forEach(analyzedSentenceInWhichPronounsAreAssociatedWithNouns -> {

            List<Token<EnglishTag>> pronounsToAssociateWithNouns = getAllPronouns(analyzedSentenceInWhichPronounsAreAssociatedWithNouns);

//            TokenWithCalculatedPoints nounOrPronounWithMaxPoints = getNounOrPronounWithMaxPoints(analyzedSentenceInWhichPronounsAreAssociatedWithNouns, sentences);
            List<TokenWithCalculatedPoints> nounsAndPronounsWithCalculatedPointsDesc = getTokensWithCalculatedPointsDesc(analyzedSentenceInWhichPronounsAreAssociatedWithNouns, sentences);

            pronounsToAssociateWithNouns.forEach(pronoun -> {

                TokenWithCalculatedPoints matchingNounOrPronounWithMaxPoints = findFirstMatchingNounOrPronoun(nounsAndPronounsWithCalculatedPointsDesc, pronoun);


                if(!nounsAndPronounsWithCalculatedPointsDesc.isEmpty()) {

                   if(isNoun(matchingNounOrPronounWithMaxPoints.getToken())) {
                        pronoun.setRoot(matchingNounOrPronounWithMaxPoints.getToken());
                        pronoun.setPoints(matchingNounOrPronounWithMaxPoints.getToken().getPoints() + matchingNounOrPronounWithMaxPoints.getCalculatedPoints());
                    }
                    else if(isPronoun(matchingNounOrPronounWithMaxPoints.getToken()) && matchingNounOrPronounWithMaxPoints.getToken().hasRoot()) {
                        pronoun.setRoot(matchingNounOrPronounWithMaxPoints.getToken().getRoot());
                        pronoun.setPoints(matchingNounOrPronounWithMaxPoints.getToken().getRoot().getPoints() + matchingNounOrPronounWithMaxPoints.getCalculatedPoints());
                    }
                    else {
                        throw new IllegalStateException("nie powinno tak być :(");
                    }
                }
            });
        });
    }

    private TokenWithCalculatedPoints findFirstMatchingNounOrPronoun(List<TokenWithCalculatedPoints> nounsAndPronounsWithCalculatedPointsDesc, Token<EnglishTag> pronoun) {
        Number[] pronounPossibleNumbers = EnglishPronoun.fromValue(pronoun.getValue()).getNumbers();
        Destination[] pronounPossibleDestinations = EnglishPronoun.fromValue(pronoun.getValue()).getDestinations();

        if (nounsAndPronounsWithCalculatedPointsDesc == null || nounsAndPronounsWithCalculatedPointsDesc.size() == 0) {
            return null;
        }

        for (TokenWithCalculatedPoints nounOrPronoun : nounsAndPronounsWithCalculatedPointsDesc) {
            List<Number> nounOrPronounPossibleNumbers = new ArrayList<>();
            List<Destination> nounOrPronounPossibleDestinations = new ArrayList<>();
            if (isNoun(nounOrPronoun.getToken())) {
               nounOrPronounPossibleNumbers = EnglishNounsDatabase.getPossibleNumbersOf(nounOrPronoun.getToken().getValue());
               nounOrPronounPossibleDestinations = EnglishNounsDatabase.getPossibleDestinationsOf(nounOrPronoun.getToken().getValue());
            }
            else if (isPronoun(nounOrPronoun.getToken())) {
               nounOrPronounPossibleNumbers = Arrays.asList(EnglishPronoun.fromValue(nounOrPronoun.getToken().getValue()).getNumbers());
               nounOrPronounPossibleDestinations = Arrays.asList(EnglishPronoun.fromValue(nounOrPronoun.getToken().getValue()).getDestinations());
            }
            else {
               System.out.println("Coś poszło nie tak :(");
            }

            if (hasCommonPart(nounOrPronounPossibleNumbers, pronounPossibleNumbers) && hasCommonPart(nounOrPronounPossibleDestinations, pronounPossibleDestinations)) {
                return nounOrPronoun;
            }
        }

        return nounsAndPronounsWithCalculatedPointsDesc.get(0);
    }

    private <K> boolean hasCommonPart(List<K> array1, K[] array2) {
        return array1
                .stream()
                .anyMatch(e1 -> Arrays.asList(array2).contains(e1));
    }


    private List<TokenWithCalculatedPoints> getTokensWithCalculatedPointsDesc(Sentence<EnglishTag> analyzedSentence, List<Sentence<EnglishTag>> sentences) {

        List<Token<EnglishTag>> nounsAndPronounsToAnalyze = getAllNounsAndPronounsToAnalyze(analyzedSentence, sentences);
        List<TokenWithCalculatedPoints> tokensWithCalculatedPointsDescToReturn = new ArrayList<>();

        for (Token<EnglishTag> currentNounOrPronoun : nounsAndPronounsToAnalyze) {
            int currentNounOrPronounPoints = currentNounOrPronoun.getPoints();
            switch (sentences.indexOf(analyzedSentence) - sentences.indexOf(currentNounOrPronoun.getSentence())) {
                case 1:
                    break;
                case 2:
                    currentNounOrPronounPoints /= 2;
                    break;
                case 3:
                    currentNounOrPronounPoints /= 4;
                    break;
                case 4:
                    currentNounOrPronounPoints /= 8;
                    break;
                default:
                    throw new IllegalStateException("nie powinno to się zdarzyć :O");
            }
            tokensWithCalculatedPointsDescToReturn.add(new TokenWithCalculatedPoints(currentNounOrPronoun, currentNounOrPronounPoints));
        }

        return tokensWithCalculatedPointsDescToReturn
                .stream().filter(t -> t.calculatedPoints > 0)
                .sorted((o1, o2) -> o2.calculatedPoints - o1.calculatedPoints)
                .collect(Collectors.toList());
    }

//    private @Nullable TokenWithCalculatedPoints getNounOrPronounWithMaxPoints(Sentence<EnglishTag> analyzedSentence, List<Sentence<EnglishTag>> sentences) {
//        List<Token<EnglishTag>> nounsAndPronounsToAnalyze = getAllNounsAndPronounsToAnalyze(analyzedSentence, sentences);
//        int maxPoints = 0;
//        Token<EnglishTag> nounOrPronounWithMaxPoints = null;
//
//        for (Token<EnglishTag> currentNounOrPronoun : nounsAndPronounsToAnalyze) {
//            int currentNounOrPronounPoints = currentNounOrPronoun.getPoints();
//            switch (sentences.indexOf(analyzedSentence) - sentences.indexOf(currentNounOrPronoun.getSentence())) {
//                case 1:
//                    break;
//                case 2:
//                    currentNounOrPronounPoints /= 2;
//                    break;
//                case 3:
//                    currentNounOrPronounPoints /= 4;
//                    break;
//                case 4:
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
//        return new TokenWithCalculatedPoints(nounOrPronounWithMaxPoints, maxPoints);
//    }

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
        for (int i = sentenceIndex - 1, j = 0; i >= 0 && j < 4; i--, j++) {
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

        Token<EnglishTag> getToken() {
            return token;
        }

        int getCalculatedPoints() {
            return calculatedPoints;
        }
    }
}
