package echo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class DialogueBox extends HBox {

    private Text text;
    private ImageView displayPicture;

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

    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    public static DialogueBox getUserDialogue(String s, Image i) {
        return new DialogueBox(s, i, true);
    }

    public static DialogueBox getEchoDialogue(String s, Image i) {
        DialogueBox db = new DialogueBox(s, i, false);
        db.flip();
        return db;
    }
}