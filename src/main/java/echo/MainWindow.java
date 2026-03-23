package echo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainWindow {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogueContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Echo echoBot;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image echoImage = new Image(this.getClass().getResourceAsStream("/images/DaEcho.png"));

    /**
     * Initialize method called automatically after FXML loads.
     */
    @FXML
    public void initialize() {
        // Scroll to the bottom when dialogContainer height changes
        dialogueContainer.heightProperty().addListener((obs) -> scrollPane.setVvalue(1.0));
        echoBot = new Echo(); // your existing Echo bot instance

        dialogueContainer.getChildren().addAll(DialogueBox.getEchoDialogue(Ui.showWelcome(), echoImage));
    }

    /**
     * Handles user input: displays user message and Echo's response.
     */
    @FXML
    private void handleUserInput() {
        dialogueContainer.setFillWidth(true);
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = echoBot.getResponse(input); // You can create this method in Echo.java

        // Add messages to VBox
        dialogueContainer.getChildren().addAll(DialogueBox.getUserDialogue(input, userImage), DialogueBox.getEchoDialogue(response, echoImage));

        userInput.clear();
    }
}