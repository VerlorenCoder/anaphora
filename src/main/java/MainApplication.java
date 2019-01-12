import javafx.application.Application;
import javafx.stage.Stage;
import pl.sgjp.morfeusz.Morfeusz;
import ui.StageManager;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        StageManager.getInstance().start(stage);
    }
}
