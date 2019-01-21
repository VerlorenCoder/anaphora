package logic.impl;

import domain.polish.PolishName;
import domain.polish.PolishSentence;
import domain.polish.PolishTag;
import domain.polish.PolishToken;

import java.util.List;

public class PolishPointArbitrator {

    private static int POINTS_FOR_NOUN = 100;
    private static int POINTS_FOR_SUBJECT = 80;
    private static int POINTS_FOR_PROPER_NOUN = 70;
    private static int POINTS_FOR_ACCUSATIVE_EMPHASIS = 50;
    private static int POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS = 40;
    private static int POINTS_FOR_NON_ADVERBIAL_EMPHASIS = 50;
    private static int POINTS_FOR_HEAD_NOUN_EMPHASIS = 80;

    public List<PolishSentence> solve(List<PolishSentence> sentences) {

        for(PolishSentence sentence : sentences) {
            findSubject(sentence);
            findHeadNounEmphasis(sentence);

            for(PolishToken token : sentence.getTokens()) {
                if (isNoun(token)) {
                    addPointsForNoun(token);
                }

                if (isProperNoun(token)) {
                    addPointsForProperNoun(token);
                }

                if (isAccusativeEmphasis(token)) {
                    addPointsForAccusativeEmphasis(token);
                }

                if (isIndirectObjectAndObliqueComplementEmphasis(token)) {
                    addPointsForIndirectObjectAndObliqueComplementEmphasis(token);
                }

                if (isNonAdverbialEmphasis(token)) {
                    addPointsForNonAdverbialEmphasis(token);
                }
            }
        }

        return sentences;
    }

    private void findSubject(PolishSentence sentence) {
        for(PolishToken token : sentence.getTokens()) {
            if(isNoun(token)) {
                addPointsForSubject(token);
            }
        }
    }

    private boolean isNoun(PolishToken token) {
        return token.getTags().contains(PolishTag.RZECZOWNIK.getAbbreviation()) ||
                token.getTags().contains(PolishTag.RZECZOWNIK_DEPRECJATYWNY.getAbbreviation());
    }

    private boolean isProperNoun(PolishToken token) {
        return (token.getTags().contains(PolishTag.RZECZOWNIK.getAbbreviation()) ||
                token.getTags().contains(PolishTag.RZECZOWNIK_DEPRECJATYWNY.getAbbreviation())) &&
                token.getName().equals(PolishName.NAZWA_WLASNA.getName());
    }

    private boolean isAccusativeEmphasis(PolishToken token) {
        return token.getTags().contains(PolishTag.PRZYPADEK_BIERNIK.getAbbreviation()) ||
                token.getTags().contains(PolishTag.PRZYPADEK_DOPELNIACZ.getAbbreviation()) ||
                token.getTags().contains(PolishTag.PRZYPADEK_NARZEDNIK.getAbbreviation());
    }

    private boolean isIndirectObjectAndObliqueComplementEmphasis(PolishToken token) {
        return token.getTags().contains(PolishTag.PRZYPADEK_CELOWNIK.getAbbreviation()) ||
                token.getTags().contains(PolishTag.PRZYPADEK_NARZEDNIK.getAbbreviation()) ||
                token.getTags().contains(PolishTag.PRZYPADEK_MIEJSCOWNIK.getAbbreviation());
    }

    private boolean isNonAdverbialEmphasis(PolishToken token) {
        return token.getTags().contains(PolishTag.PRZYSLOWEK.getAbbreviation());
    }

    private void findHeadNounEmphasis(PolishSentence sentence) {
        List<PolishToken> tokens = sentence.getTokens();
        PolishToken token, tokenCopy;

        for(int i = 0; i < sentence.getTokens().size(); i++) {
            boolean possibleConjunction = false;
            token = tokens.get(i);
            tokenCopy = tokens.get(i);

            if(isNoun(token)) {
                while(previousTokenExists(token)) {
                    PolishToken previousToken = tokens.get(token.getIndex() - 1);

                    if(isConjunction(previousToken)) {
                        possibleConjunction = true;
                    }

                    if(isNoun(previousToken)) {
                        if(!possibleConjunction) {
                            addPointForHeadNounEmphasis(tokenCopy);
                        }

                        isHeadNounEmphasis(tokens, tokenCopy, previousToken);
                        return;
                    }

                    if(isPreposition(previousToken)) {
                        return;
                    }

                    token = previousToken;
                }

                addPointForHeadNounEmphasis(tokenCopy);
            }
        }
    }

    private void isHeadNounEmphasis(List<PolishToken> tokens, PolishToken token, PolishToken previousToken) {
        boolean possibleConjunction = false;

        while(previousTokenExists(previousToken)) {
            PolishToken evenMorePreviousToken = tokens.get(previousToken.getIndex() - 1);

            if(isConjunction(evenMorePreviousToken)) {
                possibleConjunction = true;
            }

            if(isNoun(evenMorePreviousToken)) {
                if(!possibleConjunction) {
                    addPointForHeadNounEmphasis(token);
                }

                isHeadNounEmphasis(tokens, token, evenMorePreviousToken);
            }

            if(isPreposition(previousToken)) {
                return;
            }

            previousToken = evenMorePreviousToken;
        }

        addPointForHeadNounEmphasis(token);
    }





    private void addPointsForNoun(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_NOUN);
    }

    private void addPointsForSubject(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_SUBJECT);
    }

    private void addPointsForProperNoun(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_PROPER_NOUN);
    }

    private void addPointsForAccusativeEmphasis(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_ACCUSATIVE_EMPHASIS);
    }

    private void addPointsForIndirectObjectAndObliqueComplementEmphasis(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS);
    }

    private void addPointsForNonAdverbialEmphasis(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_NON_ADVERBIAL_EMPHASIS);
    }

    private void addPointForHeadNounEmphasis(PolishToken token) {
        token.setPoints(token.getPoints() + POINTS_FOR_HEAD_NOUN_EMPHASIS);
    }




    private boolean isPreposition(PolishToken token) {
        return token.getTags().contains(PolishTag.PRZYIMEK.getAbbreviation());
    }

    private boolean isConjunction(PolishToken token) {
        return token.getTags().contains(PolishTag.SPOJNIK.getAbbreviation());
    }

    private boolean previousTokenExists(PolishToken token) {
        return token.getIndex() != 0;
    }
}
