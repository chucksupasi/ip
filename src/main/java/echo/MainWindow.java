package echo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;

/**
 * Controller for the main chat window, handling user input and message display.
 */
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
     * Initializes the chat window and displays the welcome message.
     */
    @FXML
    public void initialize() {
        dialogueContainer.heightProperty().addListener((obs) -> scrollPane.setVvalue(1.0));
        echoBot = new Echo();

        dialogueContainer.getChildren().addAll(DialogueBox.getEchoDialogue(Ui.showWelcome(), echoImage));
    }

    /**
     * Processes user input and updates the dialogue with user and bot messages.
     */
    @FXML
    private void handleUserInput() {
        dialogueContainer.setFillWidth(true);
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = echoBot.getResponse(input);

        dialogueContainer.getChildren().addAll(DialogueBox.getUserDialogue(input, userImage), DialogueBox.getEchoDialogue(response, echoImage));

        userInput.clear();
    }
}