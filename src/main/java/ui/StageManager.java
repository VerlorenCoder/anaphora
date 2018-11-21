package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

import static javafx.scene.paint.Color.TRANSPARENT;

/**
 * Singleton class of StageManager
 * @author Grzegorz Oliwa, Jakub Batogowski
 */
public final class StageManager {

    private static final String TITLE = "Algorytm rozwiązujący problem zjawiska anafory";
    private static final String LOGO_PATH = "/images/logo.png";
    private final FXMLLoader loader = new FXMLLoader();
    private static StageManager instance;
    private static Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    private StageManager() {

    }

    public synchronized static StageManager getInstance() {
        if(instance == null) {
            instance = new StageManager();
        }

        return instance;
    }

    public synchronized static Stage getStage() {
        return stage;
    }

    public void start(Stage stage) {
        StageManager.stage = stage;

        try {
            initializeStage();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        setMovable(stage);
    }

    private void initializeStage() throws IOException {
        URL url = getClass().getResource("/fxml/application.fxml");
        loader.setLocation(url);

        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        Image icon = new Image(LOGO_PATH);

        scene.setFill(TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle(TITLE);
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private void setMovable(Stage stage) {
        Scene scene = stage.getScene();
        Parent parent = scene.getRoot();

        parent.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        parent.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }
}
