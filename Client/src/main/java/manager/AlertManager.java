package manager;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.List;

public class AlertManager {

    private String contentText;
    private String title;
    private String headerText;
    private Alert.AlertType type;
    private final List<ButtonType> buttons;

    public AlertManager() {
        this.contentText = "";
        this.title = "";
        this.headerText = "";
        this.type = Alert.AlertType.INFORMATION;
        this.buttons = new ArrayList<>();
    }

    public AlertManager withContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }
    public AlertManager withTitle(String title) {
        this.title = title;
        return this;
    }
    public AlertManager withHeaderText(String headerText) {
        this.headerText = headerText;
        return this;
    }
    public AlertManager withType(Alert.AlertType type) {
        this.type = type;
        return this;
    }
    public AlertManager withButton(ButtonType button) {
        this.buttons.add(button);
        return this;
    }

    public void show() {
        ButtonType[] buttons = new ButtonType[this.buttons.size()];
        Alert alert = new Alert(this.type, this.contentText, this.buttons.toArray(buttons));
        alert.setTitle(this.title);
        alert.setHeaderText(this.headerText);
        alert.show();
    }

    public ButtonType showAndWait() {
        ButtonType[] buttons = new ButtonType[this.buttons.size()];
        Alert alert = new Alert(this.type, this.contentText, this.buttons.toArray(buttons));
        alert.setTitle(this.title);
        alert.setHeaderText(this.headerText);
        alert.showAndWait();
        return alert.getResult();
    }

    public void showInAnotherThread() {
        List<ButtonType> copy = new ArrayList<>(this.buttons);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ButtonType[] buttons = new ButtonType[copy.size()];
                Alert alert = new Alert(type, contentText, copy.toArray(buttons));
                alert.setTitle(title);
                alert.setHeaderText(headerText);
                alert.show();
            }
        });
    }

}
