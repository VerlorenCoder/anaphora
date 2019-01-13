package controller;

import domain.EnglishTag;
import domain.Sentence;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import logic.*;
import logic.impl.*;
import pl.sgjp.morfeusz.Morfeusz;
import ui.MenuButtonNames;
import ui.StageManager;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private static final String MENU_TEXT_FILE_LOCALIZATION = "/text/menu-text.txt";
    private static final String BUTTON_CHOICE_IMAGE_LOCALIZATION = "/images/star.png";

    private Splitter<EnglishTag> englishSplitter = new EnglishSplitter();
    private Tokenizer tokenizer = new SimpleTokenizer();
    private Tagger<EnglishTag> englishTagger = new EnglishTagger();
    private PointArbitrator<EnglishTag> englishPointArbitrator = new EnglishPointArbitrator();
    private Morfeusz morfeusz;

    private AnaphoraResolver englishAnaphoraResolver = EnglishAnaphoraResolver
            .builder(englishSplitter, tokenizer, englishTagger, englishPointArbitrator)
            .afterSplit(sentences -> runInUserInterfaceThread(() -> displayEnglishSentences(sentences)))
            .afterTokenize(sentencesWithTokens -> runInUserInterfaceThread(() -> displayEnglishMorphologicalAnalysis(sentencesWithTokens)))
            .afterTag(sentencesWithTokens -> runInUserInterfaceThread(() -> displayEnglishMorphologicalAnalysis(sentencesWithTokens)))
            .afterPoints(sentencesWithTokens -> runInUserInterfaceThread(() -> displayEnglishMorphologicalAnalysis(sentencesWithTokens)))
            .build();

    private ImageView activeMenuOption = null;
    private Pane activePane = null;

    @FXML private ProgressBar progressBar;
    @FXML private HBox menuButton;
    @FXML private HBox algorithmEnglishButton;
    @FXML private HBox algorithmPolishButton;
    @FXML private HBox researchButton;
    @FXML private HBox contactButton;
    @FXML private HBox exitButton;
    @FXML private ImageView menuChoiceButton;
    @FXML private ImageView algorithmEnglishChoiceButton;
    @FXML private ImageView algorithmPolishChoiceButton;
    @FXML private ImageView researchChoiceButton;
    @FXML private ImageView contactChoiceButton;
    @FXML private Pane menuPane;
    @FXML private Label menuText;
    @FXML private Pane algorithmEnglishPane;
    @FXML private Pane algorithmPolishPane;
    @FXML private Pane researchPane;
    @FXML private Pane contactPane;

    @FXML private TextArea englishText;
    @FXML private TextArea englishOutput;
    @FXML private TextArea englishMorphologicalAnalysis;
    @FXML private TextArea englishSentences;
    @FXML private TextArea polishText;
    @FXML private TextArea polishOutput;
    @FXML private TextArea polishMorphologicalAnalysis;
    @FXML private TextArea polishSentences;

    @FXML
    public void initialize() {
        initializeMenu();

        //=======================================

        morfeusz = Morfeusz.createInstance();

        //=======================================
    }

    private void initializeMenu() {
        activePane = menuPane;
        activeMenuOption = menuChoiceButton;
        prepareText(MENU_TEXT_FILE_LOCALIZATION, menuText);

        menuPane.setVisible(true);
        algorithmEnglishPane.setVisible(false);
        algorithmPolishPane.setVisible(false);
        researchPane.setVisible(false);
        contactPane.setVisible(false);

        menuButton.setDisable(false);
        algorithmPolishButton.setDisable(false);
        algorithmEnglishButton.setDisable(false);
        researchButton.setDisable(true);
        contactButton.setDisable(true);
    }

    private void prepareText(String message, Label label) {
        String contactTextFilePath = getClass().getResource(message).getPath();
        File file = new File(contactTextFilePath);
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return;
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String text = bufferedReader.lines().collect(Collectors.joining("\n"));
        label.setText(text);
    }

    private void selectButton(MenuButtonNames name) {
        Glow effect = new Glow();
        effect.setLevel(0.5);

        switch(name) {
            case MAIN_MENU: {
                menuButton.setEffect(effect);
                break;
            }
            case ALGORITHM_ENG: {
                algorithmEnglishButton.setEffect(effect);
                break;
            }
            case ALGORITHM_PL: {
                algorithmPolishButton.setEffect(effect);
                break;
            }
            case RESEARCH: {
                researchButton.setEffect(effect);
                break;
            }
            case CONTACT: {
                contactButton.setEffect(effect);
                break;
            }
            case EXIT: {
                exitButton.setEffect(effect);
                break;
            }
            default: {
                System.out.println("Unsupported option!");
                break;
            }
        }
    }

    private void deselectButton(MenuButtonNames name) {
        switch (name) {
            case MAIN_MENU: {
                menuButton.setEffect(null);
                break;
            }
            case ALGORITHM_ENG: {
                algorithmEnglishButton.setEffect(null);
                break;
            }
            case ALGORITHM_PL: {
                algorithmPolishButton.setEffect(null);
                break;
            }
            case RESEARCH: {
                researchButton.setEffect(null);
                break;
            }
            case CONTACT: {
                contactButton.setEffect(null);
                break;
            }
            case EXIT: {
                exitButton.setEffect(null);
                break;
            }
            default: {
                System.out.println("Unsupported option!");
                break;
            }
        }
    }

    private void pressButton(MenuButtonNames name) {
        disableChoiceButton(activeMenuOption);
        enableChoiceButton(name);
    }

    private void enableChoiceButton(MenuButtonNames name) {
        String buttonChoicePath = getClass().getResource(BUTTON_CHOICE_IMAGE_LOCALIZATION).toExternalForm();
        Image image = new Image(buttonChoicePath);

        Pane previousPane = activePane;

        switch (name) {
            case MAIN_MENU: {
                activeMenuOption = menuChoiceButton;
                activePane = menuPane;
                break;
            }
            case ALGORITHM_ENG: {
                activeMenuOption = algorithmEnglishChoiceButton;
                activePane = algorithmEnglishPane;
                break;
            }
            case ALGORITHM_PL: {
                activeMenuOption = algorithmPolishChoiceButton;
                activePane = algorithmPolishPane;
                break;
            }
            case RESEARCH: {
                activeMenuOption = researchChoiceButton;
                activePane = researchPane;
                break;
            }
            case CONTACT: {
                activeMenuOption = contactChoiceButton;
                activePane = contactPane;
                break;
            }
            default: {
                System.out.println("Unsupported option!");
                break;
            }
        }

        activeMenuOption.setImage(image);

        if(previousPane != activePane) {
            previousPane.setVisible(false);
            activePane.setVisible(true);
        }
    }

    private void disableChoiceButton(ImageView name) {
        if(name != null) {
            name.setImage(null);
        }
    }

    @FXML void menuMouseEntered(MouseEvent event) { selectButton(MenuButtonNames.MAIN_MENU); }
    @FXML void menuMouseExited(MouseEvent event) { deselectButton(MenuButtonNames.MAIN_MENU); }
    @FXML void menuMousePressed(MouseEvent event) { pressButton(MenuButtonNames.MAIN_MENU); }
    @FXML void algorithmEnglishMouseEntered(MouseEvent event) { selectButton(MenuButtonNames.ALGORITHM_ENG); }
    @FXML void algorithmEnglishMouseExited(MouseEvent event) { deselectButton(MenuButtonNames.ALGORITHM_ENG); }
    @FXML void algorithmEnglishMousePressed(MouseEvent event) { pressButton(MenuButtonNames.ALGORITHM_ENG); }
    @FXML void algorithmPolishMouseEntered(MouseEvent event) { selectButton(MenuButtonNames.ALGORITHM_PL); }
    @FXML void algorithmPolishMouseExited(MouseEvent event) { deselectButton(MenuButtonNames.ALGORITHM_PL); }
    @FXML void algorithmPolishMousePressed(MouseEvent event) { pressButton(MenuButtonNames.ALGORITHM_PL); }
    @FXML void researchMouseEntered(MouseEvent event) { selectButton(MenuButtonNames.RESEARCH); }
    @FXML void researchMouseExited(MouseEvent event) { deselectButton(MenuButtonNames.RESEARCH); }
    @FXML void researchMousePressed(MouseEvent event) { pressButton(MenuButtonNames.RESEARCH); }
    @FXML void contactMouseEntered(MouseEvent event) { selectButton(MenuButtonNames.CONTACT); }
    @FXML void contactMouseExited(MouseEvent event) { deselectButton(MenuButtonNames.CONTACT); }
    @FXML void contactMousePressed(MouseEvent event) { pressButton(MenuButtonNames.CONTACT); }
    @FXML void exitMouseEntered(MouseEvent event) { selectButton(MenuButtonNames.EXIT); }
    @FXML void exitMouseExited(MouseEvent event) { deselectButton(MenuButtonNames.EXIT); }
    @FXML void exitMousePressed(MouseEvent event) { Platform.exit(); }

    @FXML
    void loadEnglishText() {
        File loadedFile = getFileUsingFileChooser();
        String loadedText = getText(loadedFile);
        setEnglishText(loadedText);
    }

    private void setEnglishText(String loadedText) {
        englishText.setText(loadedText);
    }

    private File getFileUsingFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik tekstowy do analizy");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt"));
        return fileChooser.showOpenDialog(StageManager.getStage());
    }

    private String getText(File file) {
        final String EMPTY_TEXT = "";

        if (file == null) {
            return EMPTY_TEXT;
        }

        try {
            return getTextFromFile(file);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return EMPTY_TEXT;
        }
    }

    private String getTextFromFile(File loadedFile) throws FileNotFoundException {
        FileReader fileReader = new FileReader(loadedFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    @FXML
    void analyzeEnglishText() {
        clearPreviouslyDisplayedData();
        String textForAnalysis = getEnglishText();
        runInBackgroundThread(() ->  englishAnaphoraResolver.analyze(textForAnalysis));
    }

    private void clearPreviouslyDisplayedData() {
        englishSentences.clear();
        englishMorphologicalAnalysis.clear();
    }

    private String getEnglishText() {
        return englishText.getText();
    }

    private <T> void displayEnglishSentences(List<Sentence<T>> sentences) {

        String text = sentences
                .stream()
                .map(sentence -> "[" + (sentences.indexOf(sentence) + 1) + "] " + sentence.getValue())
                .collect(Collectors.joining("\n"));

        englishSentences.setText(text);
        englishSentences.positionCaret(0);
    }

    private void displayEnglishMorphologicalAnalysis(List<Sentence<EnglishTag>> sentences) {


        String analyzeReport = sentences
                .stream()
                .flatMap(sentence -> sentence.getTokens().stream())
                .map(token -> {

                    final String UNKNOWN = "?";
                    final String EMPTY = "";
                    final String NEW_LINE = "\n";
                    int sentenceIndex = sentences.indexOf(token.getSentence());
                    boolean hasSentenceIndex = sentenceIndex != -1;
                    String rawToken = token.getValue();
                    boolean hasRawToken = rawToken != null;
                    int points = token.getPoints();
                    boolean hasPoints = points > 0;
                    EnglishTag tag = token.getTag();
                    boolean hasTag = tag != null;

                    return new StringBuilder()
                            .append("[").append(hasSentenceIndex ? (sentenceIndex + 1) : UNKNOWN).append("]")
                            .append(" ")
                            .append(hasRawToken ? rawToken : UNKNOWN)
                            .append(hasPoints ? "(" + points + ")" : EMPTY)
                            .append(" :: ")
                            .append(hasTag ? tag.getPolishName() : UNKNOWN)
                            .append(NEW_LINE)
                            .toString();
                })
                .collect(Collectors.joining());

        setEnglishMorphologicalAnalysisText(analyzeReport);
    }

    private void setEnglishMorphologicalAnalysisText(String text) {
        englishMorphologicalAnalysis.setText(text);
    }

    @FXML
    void loadPolishText(ActionEvent event) {
        File loadedFile = getFileUsingFileChooser();
        String loadedText = getText(loadedFile);
        setPolishText(loadedText);
    }

    private void setPolishText(String loadedText) {
        polishText.setText(loadedText);
    }

    @FXML
    void analyzePolishText(ActionEvent event) {

    }

    private void runInUserInterfaceThread(Runnable task) {
        Platform.runLater(task);
    }

    private void runInBackgroundThread(Runnable task) {
        new Thread(task).start();
    }
}
