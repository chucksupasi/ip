package echo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

/**
 * Launches the JavaFX application and displays the main chat window.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX stage and loads the main window FXML.
     * @param stage the primary stage for this application
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Echo Chatbot");
        stage.show();
    }

    /**
     * Launches the Echo JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}