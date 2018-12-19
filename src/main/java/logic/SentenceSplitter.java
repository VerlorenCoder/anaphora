package logic;

import domain.Sentence;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SentenceSplitter {

    private static final String ENGLISH_MODEL_PATH = "/binaries/en-sent.bin";

    public ArrayList<Sentence> splitIntoSentences(String text) {

        String englishSentenceDetectorModelPath = getClass().getResource(ENGLISH_MODEL_PATH).getPath();
        File file = new File(englishSentenceDetectorModelPath);
        SentenceModel sentenceModel;
        SentenceDetector sentenceDetector;
        String slittedSentences[] = new String[0];

        try {
            sentenceModel = new SentenceModel(file);
            sentenceDetector = new SentenceDetectorME(sentenceModel);
            slittedSentences = sentenceDetector.sentDetect(text);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ArrayList<Sentence> sentencesToReturn = new ArrayList<>();
        for (int i = 0; i < slittedSentences.length; i++) {
            sentencesToReturn.add(
                    Sentence.builder()
                            .content(slittedSentences[i])
                            .index(i + 1)
                            .build());
        }
        return sentencesToReturn;
    }
}
