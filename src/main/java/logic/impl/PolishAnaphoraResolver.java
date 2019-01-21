package logic.impl;

import domain.polish.PolishSentence;
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
                polishToken.setIndex(counter++);
                polishToken.setSentenceNumber(i);

                polishSentence.getTokens().add(polishToken);
            }

            preparedSentences.add(polishSentence);
            counter = 0;
        }

        return preparedSentences;
    }
}
