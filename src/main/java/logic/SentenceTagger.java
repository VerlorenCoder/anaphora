package logic;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import java.io.File;
import java.io.IOException;

public class SentenceTagger {

    private static final String ENGLISH_MODEL_PATH = "/binaries/en-pos-maxent.bin";

    public String[] getTags(String[] tokens) {

        String englishTaggerModelPath = getClass().getResource(ENGLISH_MODEL_PATH).getPath();
        File file = new File(englishTaggerModelPath);

        POSModel posModel;
        POSTagger posTagger;
        String tags[] = new String[0];

        try {
            posModel = new POSModel(file);
            posTagger = new POSTaggerME(posModel);
            tags = posTagger.tag(tokens);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return tags;
    }
}
