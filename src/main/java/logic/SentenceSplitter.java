package logic;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.File;
import java.io.IOException;

public class SentenceSplitter {
    private static final String ENGLISH_MODEL_PATH = "/binaries/en-sent.bin";

    public String[] splitIntoSentences(String text) {
        String englishSentenceDetectorModelPath = getClass().getResource(ENGLISH_MODEL_PATH).getPath();
        File file = new File(englishSentenceDetectorModelPath);
        SentenceModel sentenceModel;
        SentenceDetector sentenceDetector;
        String sentences[] = new String[0];

        try {
            sentenceModel = new SentenceModel(file);
            sentenceDetector = new SentenceDetectorME(sentenceModel);
            sentences = sentenceDetector.sentDetect(text);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return sentences;
    }
}
