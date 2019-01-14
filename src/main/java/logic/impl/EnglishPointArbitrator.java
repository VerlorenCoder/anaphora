package logic.impl;

import domain.EnglishTag;
import domain.Sentence;
import domain.Token;
import logic.PointArbitrator;

import java.util.List;

public class EnglishPointArbitrator implements PointArbitrator<EnglishTag> {

    private static int POINTS_FOR_NOUN = 100;
    private static int POINTS_FOR_SUBJECT = 80;
    private static int POINTS_FOR_PROPER_NOUN = 70;
    private static int POINTS_FOR_ACCUSATIVE_EMPHASIS = 50;
    private static int POINTS_FOR_INDIRECT_OBJECT_AND_OBLIQUE_COMPLEMENT_EMPHASIS = 40;
    private static int POINTS_FOR_NON_ADVERBIAL_EMPHASIS = 40;
    private static int POINTS_FOR_HEAD_NOUN_EMPHASIS = 80;

    @Override
    public void admitPoints(List<Sentence<EnglishTag>> sentences) {
        sentences.forEach(this::admitPoints);
    }

    private void admitPoints(Sentence<EnglishTag> sentence) {

        // [80 pkt] [tags 12-15] Subject (first noun in a sentence)
        if (hasSubject(sentence)) {
            Token<EnglishTag> subject = getSubject(sentence);
            addPointsForSubject(subject);
        }

        for (Token<EnglishTag> token : sentence.getTokens()) {
            // [100 pkt] [tags 12-15] Noun
            if (isNoun(token)) {
                addPointsForNoun(token);
            }

            // [70 pkt] [tags 14-15] Proper Nouns
            if (isProperNoun(token)) {
                addPointsForProperNoun(token);
            }

            // [50 pkt] [tags 3 + (7-9) + 12-15] Accusative Emphasis
            if (isAccusativeEmphasis(token)) {
                addPointsForAccusativeEmphasis(token);
            }

            // [40 pkt] [(tags 19 + 12-15) || ("of" + (3) + (7-9) + 12-15)] Indirect Object and Oblique Complement Emphasis
            if (isIndirectObjectAndObliqueComplementEmphasis(token)) {
                addPointsForIndirectObjectAndObliqueComplementEmphasis(token);
            }

            // [50 pkt] [tags !("of" + (3) + (7-9) + 12-15)] Non Adverbial Emphasis
            if (isNonAdverbialEmphasis(token)) {
                addPointsForNonAdverbialEmphasis(token);
            }

            // [80 pkt] Head Noun Emphasis
            if (isHeadNounEmphasis(token)) {
                addPointForHeadNounEmphasis(token);
            }
        }
    }




    private boolean hasSubject(Sentence<?> sentence) {
        return sentence
                .getTokens()
                .stream()
                .anyMatch(this::isNoun);
    }

    private Token<EnglishTag> getSubject(Sentence<EnglishTag> sentence) {
        return sentence
                .getTokens()
                .stream()
                .filter(this::isNoun)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    private void addPointsForSubject(Token token) {
        token.addPoints(POINTS_FOR_SUBJECT);
    }




    private boolean isNoun(Token token) {
        return token.getTag().equals(EnglishTag.NOUN_SINGULAR_OR_GROUP)
                || token.getTag().equals(EnglishTag.NOUN_PLURAL)
                || token.getTag().equals(EnglishTag.PROPER_NOUN_SINGULAR)
                || token.getTag().equals(EnglishTag.PROPER_NOUN_PLURAL);
    }

    private void addPointsForNoun(Token token) {
        token.addPoints(POINTS_FOR_NOUN);
    }




    private boolean isProperNoun(Token token) {
        return token.getTag().equals(EnglishTag.PROPER_NOUN_SINGULAR)
                || token.getTag().equals(EnglishTag.PROPER_NOUN_PLURAL);
    }

    private void addPointsForProperNoun(Token token) {
        token.addPoints(POINTS_FOR_PROPER_NOUN);
    }




    private boolean isAccusativeEmphasis(Token<EnglishTag> analyzedToken) {
        if (isNoun(analyzedToken)) {    // tag: 12-15
            while (hasPreviousToken(analyzedToken)) {
                Token<EnglishTag> previousToken = getPreviousToken(analyzedToken);
                if (isDeterminer(previousToken)) {          // tag: 3
                    return true;
                }
                else if (isNotAdjective(previousToken)){    // tag: !(7-9)
                    return false;
                }
                analyzedToken = previousToken;
            }
        }
        return false;
    }

    private boolean hasPreviousToken(Token<EnglishTag> token) {
        return token.getSentence().getTokens().indexOf(token) > 0;
    }

    private Token<EnglishTag> getPreviousToken(Token<EnglishTag> token) {
        int tokenIndex = token.getSentence().getTokens().indexOf(token);
        int previousTokenIndex = tokenIndex - 1;
        return token.getSentence().getTokens().get(previousTokenIndex);
    }

    private boolean isNotAdjective(Token token) {
        return !(token.getTag().equals(EnglishTag.ADJECTIVE)
                || token.getTag().equals(EnglishTag.ADJECTIVE_COMPARATIVE)
                || token.getTag().equals(EnglishTag.ADJECTIVE_SUPERLATIVE));
    }

    private boolean isDeterminer(Token token) {
        return token.getTag().equals(EnglishTag.DETERMINER);
    }

    private void addPointsForAccusativeEmphasis(Token token) {
        token.addPoints(POINTS_FOR_ACCUSATIVE_EMPHASIS);
    }




    private boolean isIndirectObjectAndObliqueComplementEmphasis(Token<EnglishTag> analyzedToken) {
        if (isNoun(analyzedToken)) {    // tag: 12-15
            if (hasPreviousToken(analyzedToken)) {     // possibility #1 - tags: 19 + 12-15
                Token<EnglishTag> previousToken = getPreviousToken(analyzedToken);
                if (isPossessivePronoun(previousToken)) {
                    return true;
                }
            }
            while (hasPreviousToken(analyzedToken)) {  // possibility #2 - tags: "of" + (3) + (7-9) + 12-15
                Token<EnglishTag> previousToken = getPreviousToken(analyzedToken);
                if (isOfWord(previousToken)) {  // word: "of"
                    return true;
                }
                else if (isNotAdjective(previousToken) && isNotDeterminer(previousToken)){    // tag: !(3, 7-9)
                    return false;
                }
                analyzedToken = previousToken;
            }
        }
        return false;
    }

    private boolean isPossessivePronoun(Token token) {
        return token.getTag().equals(EnglishTag.POSSESSIVE_PRONOUN);
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




    private boolean isNonAdverbialEmphasis(Token<EnglishTag> analyzedToken) {
        if (isNoun(analyzedToken)) {    // tag: 12-15
            while (hasPreviousToken(analyzedToken)) {
                Token<EnglishTag> previousToken = getPreviousToken(analyzedToken);
                if (isOfWord(previousToken)) {  // word: "of"
                    return false;
                }
                else if (isNotAdjective(previousToken) && isNotDeterminer(previousToken)){    // tag: !(3, 7-9)
                    return true;
                }
                analyzedToken = previousToken;
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




    private boolean isHeadNounEmphasis(Token<EnglishTag> analyzedToken) {
        // ToDo: Grześku, proszę to zaimplementować :)
        return false;
    }

    private void addPointForHeadNounEmphasis(Token token) {
        token.addPoints(POINTS_FOR_HEAD_NOUN_EMPHASIS);
    }
}
