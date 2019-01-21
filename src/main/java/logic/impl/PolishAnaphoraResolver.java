package logic.impl;

import domain.polish.PolishSentence;
import domain.polish.PolishTag;
import domain.polish.PolishToken;
import pl.sgjp.morfeusz.Morfeusz;
import pl.sgjp.morfeusz.MorphInterpretation;

import java.util.ArrayList;
import java.util.List;

public class PolishAnaphoraResolver {

    private Morfeusz morfeusz = Morfeusz.createInstance();
    private PolishPointArbitrator polishPointArbitrator = new PolishPointArbitrator();

    public List<PolishSentence> resolve(String textForAnalysis) {
        List<String> rawSentences = PolishSplitter.split(textForAnalysis);
        List<PolishSentence> preparedSentences = prepareSentences(rawSentences);
        List<PolishSentence> result = polishPointArbitrator.solve(preparedSentences);

        return result;
    }

    private List<PolishSentence> prepareSentences(List<String> sentences) {
        List<PolishSentence> preparedSentences = new ArrayList<>();

        int counter = 0;
        for(int i = 0; i < sentences.size(); i++) {
            PolishSentence polishSentence = new PolishSentence();
            String sentence = sentences.get(i);
            polishSentence.setValue(sentence);
            List<MorphInterpretation> morphInterpretations = morfeusz.analyseAsList(sentence);

            for(MorphInterpretation interpretation : morphInterpretations) {
                PolishToken polishToken = new PolishToken();
                polishToken.setName(interpretation.getName(morfeusz));
                polishToken.setWord(interpretation.getOrth());
                polishToken.setTags(interpretation.getTag(morfeusz));
                polishToken.setLexemIndex(interpretation.getStartNode());
                polishToken.setIndex(counter++);
                polishToken.setSentenceNumber(i);

                polishSentence.getTokens().add(polishToken);
            }

            preparedSentences.add(polishSentence);
            counter = 0;
        }

        return preparedSentences;
    }

    public List<PolishSentence> algorithm(List<PolishSentence> sentences) {
        if(sentences.size() == 1) {
            return sentences;
        }

        List<PolishToken> nouns = new ArrayList<>();
        List<PolishToken> newNouns = new ArrayList<>();

        for(PolishToken token : sentences.get(0).getTokens()) {
            if(token.getTags().contains(PolishTag.RZECZOWNIK.getAbbreviation())
                    || token.getTags().contains(PolishTag.RZECZOWNIK_DEPRECJATYWNY.getAbbreviation())) {
                nouns.add(token);
            }
        }

        for(int i = 1; i < sentences.size(); i++) {
            List<PolishToken> nounsCopy;

            for(PolishToken token : sentences.get(i).getTokens()) {
                if(token.getTags().contains(PolishTag.ZAIMEK_NIETRZECIOOSOBOWY.getAbbreviation()) ||
                        token.getTags().contains(PolishTag.ZAIMEK_TRZECIOOSOBOWY.getAbbreviation()) ||
                        token.getTags().contains(PolishTag.ZAIMEK_SIEBIE.getAbbreviation())) {

                    if(token.getWord().equals("nie")) {
                        continue;
                    }

                    nounsCopy = new ArrayList<>(nouns);
                    while(!nounsCopy.isEmpty()) {
                        PolishToken theBestToken = theBestToken(nounsCopy);

                        if (hasSameType(token, theBestToken)) {
                            token.setRoot(theBestToken.getWord());
                            token.setPoints(token.getPoints() + theBestToken.getPoints() / 2);
                            nounsCopy = new ArrayList<>(nouns);
                            newNouns.add(token);
                            break;
                        } else {
                            nounsCopy.remove(theBestToken);
                        }
                    }
                }
            }

            nouns.addAll(newNouns);
            newNouns.clear();
        }

        return sentences;
    }

    private PolishToken theBestToken(List<PolishToken> tokens) {
        PolishToken result = new PolishToken();

        for(PolishToken token : tokens) {
            if(token.getPoints() > result.getPoints()) {
                result = token;
            }
        }

        return result;
    }

    private boolean hasSameType(PolishToken currentToken, PolishToken theBestToken) {
        boolean hasM1 = currentToken.getTags().contains(PolishTag.RODZAJ_MESKI_OSOBOWY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_MESKI_OSOBOWY.getAbbreviation());
        boolean hasM2 = currentToken.getTags().contains(PolishTag.RODZAJ_MESKI_ZWIERZECY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_MESKI_ZWIERZECY.getAbbreviation());
        boolean hasM3 = currentToken.getTags().contains(PolishTag.RODZAJ_MESKI_RZECZOWY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_MESKI_RZECZOWY.getAbbreviation());
        boolean hasF = currentToken.getTags().contains(PolishTag.RODZAJ_ZENSKI.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_ZENSKI.getAbbreviation());
        boolean hasN1 = currentToken.getTags().contains(PolishTag.RODZAJ_NIJAKI_ZBIOROWY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_NIJAKI_ZBIOROWY.getAbbreviation());
        boolean hasN2 = currentToken.getTags().contains(PolishTag.RODZAJ_NIJAKI_ZWYKLY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_NIJAKI_ZWYKLY.getAbbreviation());
        boolean hasP1 = currentToken.getTags().contains(PolishTag.RODZAJ_MESKI_OSOBOWY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_MESKI_OSOBOWY.getAbbreviation());
        boolean hasP2 = currentToken.getTags().contains(PolishTag.RODZAJ_PRZYMNOGI_ZWYKLY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_PRZYMNOGI_ZWYKLY.getAbbreviation());
        boolean hasP3 = currentToken.getTags().contains(PolishTag.RODZAJ_PRZYMNOGI_OPISOWY.getAbbreviation()) &&
                theBestToken.getTags().contains(PolishTag.RODZAJ_PRZYMNOGI_OPISOWY.getAbbreviation());

        return hasM1 || hasM2 || hasM3 || hasF || hasN1 || hasN2 || hasP1 || hasP2 || hasP3;
    }
}
