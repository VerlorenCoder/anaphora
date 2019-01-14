package logic.impl;

import domain.EnglishTag;
import domain.Sentence;
import logic.Splitter;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnglishSplitter implements Splitter<EnglishTag> {

    private static final String ENGLISH_MODEL_PATH = "/binaries/en-sent.bin";

    private SentenceDetector sentenceDetector;


    public EnglishSplitter() {

        try {
            init();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Couldn't initialize new object", e);
        }
    }

    private void init() throws IOException {
        String englishSentenceDetectorModelPath = getClass().getResource(ENGLISH_MODEL_PATH).getPath();
        File file = new File(englishSentenceDetectorModelPath);
        SentenceModel sentenceModel = new SentenceModel(file);
        sentenceDetector = new SentenceDetectorME(sentenceModel);
    }


    @Override
    public List<Sentence<EnglishTag>> split(String text) {
        String sentences[] = sentenceDetector.sentDetect(text);
        return buildSentences(sentences);
    }

    private List<Sentence<EnglishTag>> buildSentences(String[] rawSentences) {
        return Arrays.stream(rawSentences)
                .map(rawSentence -> new Sentence<EnglishTag>(rawSentence))
                .collect(Collectors.toList());
    }
}
