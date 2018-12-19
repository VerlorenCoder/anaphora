package controller;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import logic.SentenceSplitter;
import logic.SentenceTagger;
import logic.Tokenizer;
import ui.MenuButtonNames;
import ui.StageManager;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainController {

    private static final String menuTextFileLocalization = "/text/menu-text.txt";
    private static final String buttonChoiceImageLocalization = "/images/star.png";
    private ImageView activeMenuOption = null;
    private Pane activePane = null;

    @FXML
    public void initialize() {
        initializeMenu();
    }

    private void initializeMenu() {
        activePane = menuPane;
        activeMenuOption = menuChoiceButton;

        menuPane.setVisible(true);
        algorithmEnglishPane.setVisible(false);
        algorithmPolishPane.setVisible(false);
        researchPane.setVisible(false);
        contactPane.setVisible(false);

        prepareText(menuTextFileLocalization, menuText);
    }

    private void prepareText(String message, Label label) {
        String contactTextFilePath = getClass().getResource(message).getPath();
        File file = new File(contactTextFilePath);
        FileReader fileReader;

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
        String buttonChoicePath = getClass().getResource(buttonChoiceImageLocalization).toExternalForm();
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
    void loadEnglishText(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik tekstowy do analizy");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt"));
        File file = fileChooser.showOpenDialog(StageManager.getStage());

        if(file != null) {
            FileReader fileReader = null;

            try {
                fileReader = new FileReader(file);
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String text = bufferedReader.lines().collect(Collectors.joining("\n"));
            englishText.setText(text);
        }
    }

    @FXML
    void analyzeEnglishText(ActionEvent event) {
        englishSentences.clear();
		englishMorphologicalAnalysis.clear();

        SentenceSplitter sentenceSplitter = new SentenceSplitter();
        String text = englishText.getText();
        String[] sentences = sentenceSplitter.splitIntoSentences(text);

        for(int i = 1; i <= sentences.length; i++) {
            englishSentences.appendText("[" + i + "] " + sentences[i-1] + "\n\n");
        }

        englishSentences.positionCaret(0);

        // Morphological analysis
        Tokenizer tokenizer = new Tokenizer();
        SentenceTagger sentenceTagger = new SentenceTagger();

        ArrayList<String> nounTags = new ArrayList<>();
        nounTags.add("NN");
        nounTags.add("NNS");
        nounTags.add("NNP");
        nounTags.add("NNPS");

        ArrayList<String> pronounTags = new ArrayList<>();
        pronounTags.add("PRP");
        pronounTags.add("PRP$");

        int nounCounter = 1;
        int pronounCounter = 1;
        for(int i = 0; i < sentences.length; i++) {
            String[] tokens = tokenizer.simpleTokenization(sentences[i]);
            String[] tags = sentenceTagger.tagSentence(tokens);

            for(int j = 0; j < tokens.length; j++) {
                englishMorphologicalAnalysis.appendText(tokens[j] + " :: " + tags[j] + "\n");
                if(nounTags.contains(tags[j])) {
                    englishOutput.appendText("[N" + Integer.toString(nounCounter) + "] ");
                    nounCounter++;
                } else if(pronounTags.contains(tags[j])) {
                    englishOutput.appendText("[P" + Integer.toString(pronounCounter) + "] ");
                    pronounCounter++;
                }
                englishOutput.appendText(tokens[j] + " ");
            }

            englishMorphologicalAnalysis.appendText("\n");
        }
    }

    @FXML
    void loadPolishText(ActionEvent event) {

    }

    @FXML
    void analyzePolishText(ActionEvent event) {

    }
}
