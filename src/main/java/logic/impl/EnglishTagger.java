package logic.impl;

import com.sun.istack.internal.Nullable;
import domain.english.EnglishTag;
import domain.english.Sentence;
import domain.english.Token;
import logic.Tagger;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EnglishTagger implements Tagger<EnglishTag> {

    private static final String ENGLISH_MODEL_PATH = "/binaries/en-pos-maxent.bin";

    private POSTagger posTagger;

    public EnglishTagger() {
        try {
            init();
        }
        catch (IOException e) {
            throw new IllegalStateException("Couldn't initialize new object", e);
        }
    }

    private void init() throws IOException {
        String englishTaggerModelPath = getClass().getResource(ENGLISH_MODEL_PATH).getPath();
        File file = new File(englishTaggerModelPath);
        POSModel posModel = new POSModel(file);
        posTagger = new POSTaggerME(posModel);
    }


    @Override
    public void tag(List<Sentence<EnglishTag>> sentences) {

        List<Token<EnglishTag>> tokens = sentences
                .stream()
                .flatMap(sentence -> sentence.getTokens().stream())
                .collect(Collectors.toList());

        String[] rawTokens = tokens
                .stream()
                .map(Token::getValue)
                .toArray(String[]::new);

        String[] rawTags = posTagger.tag(rawTokens);

        List<EnglishTag> tags = Arrays.stream(rawTags)
                .map(EnglishTag::fromAbbreviation)
                .collect(Collectors.toList());

        validateEqualityOfSizes(tokens, tags);

        for (int i = 0; i < tokens.size(); i++) {
            tokens.get(i).setTag(tags.get(i));
        }

        mergeProperNouns(sentences);
    }

    private void validateEqualityOfSizes(List<?> list1, List<?> list2) {
        if (list1.size() != list2.size()) {
            throw new IllegalStateException("The size of lists aren't equal.");
        }
    }

    private void mergeProperNouns(List<Sentence<EnglishTag>> sentences) {

        for (Sentence<EnglishTag> currentSentence : sentences) {
            int currentSentenceTokensSize = currentSentence.getTokens().size();

            for (int previousTokenIndex = 0, currentTokenIndex = 1;
                 currentTokenIndex < currentSentenceTokensSize;
                 previousTokenIndex++, currentTokenIndex++) {

                Token<EnglishTag> previousToken = currentSentence.getTokens().get(previousTokenIndex);
                Token<EnglishTag> currentToken = currentSentence.getTokens().get(currentTokenIndex);

                EnglishTag previousTag = previousToken.getTag();
                EnglishTag currentTag = currentToken.getTag();

                String previousRawToken = previousToken.getValue();
                String currentRawToken = currentToken.getValue();

                if (isProperNoun(currentTag) && isProperNoun(previousTag)) {
                    currentToken.setValue(previousRawToken + " " + currentRawToken);

                    if (!haveSameTags(currentTag, previousTag)) {
                        currentToken.setTag(EnglishTag.PROPER_NOUN_PLURAL);
                    }
                    currentSentence.getTokens().remove(previousToken);
                }
            }
        }
    }

    private boolean isProperNoun(@Nullable final EnglishTag tag) {
        return EnglishTag.PROPER_NOUN_SINGULAR.equals(tag)
                || EnglishTag.PROPER_NOUN_PLURAL.equals(tag);
    }

    private boolean haveSameTags(EnglishTag tag1, EnglishTag tag2) {
        return Objects.equals(tag1, tag2);
    }
}
