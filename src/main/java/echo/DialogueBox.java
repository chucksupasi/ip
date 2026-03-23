package echo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Represents a single chat message in the dialogue, with text and avatar.
 */
public class DialogueBox extends HBox {

    private Text text;
    private ImageView displayPicture;

    /**
     * Creates a new dialogue box with text and image.
     * @param s the message text
     * @param i the avatar image
     * @param isUser true if this message is from the user, false if from the bot
     */
    public DialogueBox(String s, Image i, boolean isUser) {
        displayPicture = new ImageView(i);

        text = new Text(s);
        text.setWrappingWidth(400);

        if (isUser) {
            text.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
            HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);
        }
        else {
            text.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
            HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);
        }

        displayPicture.setFitWidth(50);
        displayPicture.setFitHeight(50);

        this.setSpacing(10);

        this.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Flips the dialogue box horizontally for bot messages.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /**
     * Creates a dialogue box for user messages.
     * @param s the message text
     * @param i the user avatar image
     * @return a DialogueBox instance for the user
     */
    public static DialogueBox getUserDialogue(String s, Image i) {
        return new DialogueBox(s, i, true);
    }

    /**
     * Creates a dialogue box for bot messages.
     * @param s the message text
     * @param i the bot avatar image
     * @return a DialogueBox instance for the bot
     */
    public static DialogueBox getEchoDialogue(String s, Image i) {
        DialogueBox db = new DialogueBox(s, i, false);
        db.flip();
        return db;
    }
}