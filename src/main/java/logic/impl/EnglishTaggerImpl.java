package logic.impl;

import domain.Tag;
import domain.Token;
import logic.Tagger;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnglishTaggerImpl implements Tagger {

    private static final String ENGLISH_MODEL_PATH = "/binaries/en-pos-maxent.bin";

    @Override
    public void addTags(List<Token> tokens) {

        String englishTaggerModelPath = getClass().getResource(ENGLISH_MODEL_PATH).getPath();
        File file = new File(englishTaggerModelPath);

        POSModel posModel;
        POSTagger posTagger;
        String tags[] = new String[0];

        try {
            posModel = new POSModel(file);
            posTagger = new POSTaggerME(posModel);
            tags = posTagger.tag(tokens.stream().map(Token::getValue).toArray(String[]::new));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        mergeTokensWithTags(tokens, tags);
        mergeProperNouns(tokens);
    }

    private void mergeTokensWithTags(List<Token> tokens, String[] tags) {
        for (int i = 0; i < tokens.size(); i++) {
            tokens.get(i).setTag(Tag.fromAbbreviation(tags[i]));
        }
    }

    private void mergeProperNouns(List<Token> tokens) {
        for (int previousToken = 0, currentToken = 1; currentToken < tokens.size(); previousToken++, currentToken++) {
            if (isProperNoun(tokens.get(previousToken)) && isProperNoun(tokens.get(currentToken))) {
                merge(tokens.get(previousToken), tokens.get(currentToken), tokens);
            }
        }
    }

    private boolean isProperNoun(Token token) {
        return token.getTag().equals(Tag.PROPER_NOUN_SINGULAR) || token.getTag().equals(Tag.PROPER_NOUN_PLURAL);
    }

    private void merge(Token firstToken, Token secondToken, List<Token> tokens) {
        if (haveSameTags(firstToken, secondToken)) {
            firstToken.setValue(mergeTokensValues(firstToken, secondToken));
            tokens.remove(secondToken);
        }
        else {
            firstToken.setValue(mergeTokensValues(firstToken, secondToken));
            firstToken.setTag(Tag.PROPER_NOUN_PLURAL);
            tokens.remove(secondToken);
        }
    }

    private boolean haveSameTags(Token firstToken, Token secondToken) {
        return firstToken.getTag().equals(secondToken.getTag());
    }

    private String mergeTokensValues(Token firstToken, Token secondToken) {
        return firstToken.getValue() + " " + secondToken.getValue();
    }
}
