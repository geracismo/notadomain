package controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import manager.AlertManager;
import manager.SocketManager;
import model.AbstractList;
import model.Email;
import model.TypeOfData;
import model.User;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;
import utility.EmailUtility;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MailBoxController implements Initializable, EventHandler<KeyEvent>, ResponseController {

    @FXML
    private Pane mainPane;
    @FXML
    private TextArea toTxt;
    @FXML
    private TextArea ccTxt;
    @FXML
    private TextArea subjectTxt;
    @FXML
    private TextArea bodyTxt;
    @FXML
    private HBox actionHBox;
    @FXML
    private ImageView sendBtn;
    @FXML
    private ImageView trashBtn;
    @FXML
    private ImageView allegatoBtn;


    private Email emailToSend;
    private AbstractList emailList;
    private TypeOfData typeOfList;
    private User user;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tooltip.install(sendBtn, new Tooltip("Invia"));
        Tooltip.install(trashBtn, new Tooltip("Elimina"));
        Tooltip.install(allegatoBtn, new Tooltip("Allega"));

        this.toTxt.setOnKeyPressed(this);
        this.ccTxt.setOnKeyPressed(this);
        this.subjectTxt.setOnKeyPressed(this);
    }

    public void initController(AbstractList sharedList, User user, Email emailToSend) {
        this.initController(sharedList, user, emailToSend, null);
    }

    public void initController(AbstractList sharedList, User user, Email emailToSend, TypeOfData type) {
        if(sharedList == null)
            return;
        this.user = user;
        this.typeOfList = type;

        this.emailList = sharedList;

        if(emailToSend == null) {
            this.emailToSend = new Email();
        } else {
            this.emailToSend = emailToSend;
        }
        this.emailToSend.setMitt(user.getMail());

        //bindings
        toTxt.textProperty().bindBidirectional(this.emailToSend.destProperty());
        ccTxt.textProperty().bindBidirectional(this.emailToSend.ccProperty());
        subjectTxt.textProperty().bindBidirectional(this.emailToSend.subjectProperty());
        bodyTxt.textProperty().bindBidirectional(this.emailToSend.bodyProperty());
    }

    @FXML
    public void onDelete() {
        if(typeOfList == TypeOfData.BOZZE) {
            try {
                EmailUtility.removeFromFile(emailToSend, this.user, typeOfList);
                mainPane.setVisible(false);
                this.emailList.getEmailList().remove(emailToSend);
                this.emailList.setCurrentEmail(null);
            } catch (Exception e) {
                new AlertManager()
                        .withType(Alert.AlertType.ERROR)
                        .withTitle("Errore eliminazione")
                        .withHeaderText("É stato riscontrato un errore durante l'eliminazione della bozza")
                        .withButton(ButtonType.OK).show();
            }
            return;
        }

        if (emailToSend.isEmpty()) {
            mainPane.setVisible(false);
        } else {
            ButtonType userResponse = new AlertManager()
                    .withType(Alert.AlertType.CONFIRMATION)
                    .withTitle("Salvare in bozze")
                    .withHeaderText("Vuoi salvare il messaggio nelle bozze?")
                    .withButton(ButtonType.YES).withButton(ButtonType.NO).withButton(ButtonType.CANCEL).showAndWait();
            if (userResponse == ButtonType.NO) {
                mainPane.setVisible(false);
            } else if (userResponse == ButtonType.YES) {
                //salvare in bozze
                try {
                    EmailUtility.addToFile(emailToSend, this.user, TypeOfData.BOZZE);
                    mainPane.setVisible(false);
                } catch (Exception e) {
                    new AlertManager()
                            .withType(Alert.AlertType.ERROR)
                            .withTitle("Impossibile salvare")
                            .withHeaderText("Si è verificato un errore durante il salvataggio nelle bozze")
                            .withButton(ButtonType.OK).show();
                }
            }
        }
    }

    public void resizeMailBox(double widthValue) { bodyTxt.setMaxWidth(widthValue); }
    public void setActionBarColor(String hexColor) { actionHBox.setStyle("-fx-background-color: " + hexColor); }

    @FXML
    public void onSendMail() {
        List<String> notValidEmail = new ArrayList<>();

        if(this.emailToSend.getDest() != null) {
            this.emailToSend.setDest(this.emailToSend.getDest().trim().replaceAll(" ", ""));
            List<String> listOfReceipts = EmailUtility.getAllRecipients(this.emailToSend.getDest());
            if(listOfReceipts.size() == 0)
                this.emailToSend.setDest(null);
            for(String destinatario : listOfReceipts) {
                if(!EmailUtility.isValidMail(destinatario))
                    notValidEmail.add(destinatario);
            }
        }

        if(this.emailToSend.getCc() != null) {
            this.emailToSend.setCc(this.emailToSend.getCc().trim().replaceAll(" ", ""));
            for(String destinatario : EmailUtility.getAllRecipients(this.emailToSend.getCc())) {
                if(!EmailUtility.isValidMail(destinatario))
                    notValidEmail.add(destinatario);
            }
        }

        if(notValidEmail.size() > 0) {
            StringBuilder errMessage = new StringBuilder("Le seguenti email non sono valide:\n");
            for (String notValid : notValidEmail) {
                if(!notValid.isBlank() && !notValid.isEmpty())
                    errMessage.append("- ").append(notValid).append("\n");
            }
            new AlertManager().withType(Alert.AlertType.ERROR).withHeaderText(errMessage.toString()).withTitle("Email non valida.").withButton(ButtonType.OK).show();
        } else if(this.emailToSend.getDest() == null) {
            new AlertManager().withType(Alert.AlertType.ERROR).withHeaderText("Deve essere presente almeno un destinatario").withTitle("Email non valida.").withButton(ButtonType.OK).show();
        } else {
            emailToSend.setDate(new Date());
            new SocketManager(exec, new ClientRequest(TypeOfRequest.SEND_EMAIL, this.user, emailToSend), this);
        }
    }

    @FXML
    public void onAttach() {
        new AlertManager().withTitle("Funzione allegato").withType(Alert.AlertType.INFORMATION).withButton(ButtonType.OK).withHeaderText("La funzione non è stata implementata per mancanza di tempo.\nAl click su questa icona si sarebbe aperto il FileChooser" +
                " che avrebbe dato \nla possibilità all'utente di selezionare il file da allegare." + "\n" +
                "Il file sarebbe stato convertito in base64 e inserito nella classe Email, trasferita col socket").show();
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getText().equals(" ")) {
            TextArea tmp = (TextArea) event.getSource();
            if(!tmp.getId().equalsIgnoreCase("subjectTxt"))
                tmp.appendText("; ");
        } else if(event.getCode() == KeyCode.TAB) {
            TextArea tmp = (TextArea) event.getSource();
            switch (tmp.getId()) {
                case "toTxt":
                    toTxt.setText(toTxt.getText() != null ? toTxt.getText().trim() : null);
                    ccTxt.requestFocus();
                    break;
                case "ccTxt":
                    ccTxt.setText(ccTxt.getText() != null ? ccTxt.getText().trim() : null);
                    subjectTxt.requestFocus();
                    break;
                case "subjectTxt":
                    subjectTxt.setText(subjectTxt.getText() != null ? subjectTxt.getText().trim() : null);
                    bodyTxt.requestFocus();
                    break;
            }
        }
    }

    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainPane.setVisible(false);
                if(typeOfList == TypeOfData.BOZZE)
                    onDelete();
            }
        });
        new AlertManager().withType(Alert.AlertType.INFORMATION).withTitle("Invio riuscito").withHeaderText("L'invio della mail é avvenuto correttamente.").withButton(ButtonType.OK).showInAnotherThread();
    }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) {
        if(response.getStatus() == ResponseStatus.RECIPIENTS_UNKNOWN) {
            StringBuilder emailUnknowns = new StringBuilder();
            for(Email e : response.getResultList())
                emailUnknowns.append(e.getDest()).append("\n");
            new AlertManager().withButton(ButtonType.OK).withHeaderText("Le seguenti mail, inserite come destinatario o cc non esistono:\n" + emailUnknowns.toString()).withTitle("Alcune email non esistono").withType(Alert.AlertType.WARNING).showInAnotherThread();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mainPane.setVisible(false);
                }
            });
        }else {
            new AlertManager().withButton(ButtonType.OK).withHeaderText(response.getDescription()).withTitle("Errore durante l'invio della email").withType(Alert.AlertType.ERROR).showInAnotherThread();
        }
    }

    @Override
    public void onSocketError(Exception e, String errorMessage) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText(errorMessage == null ? e.getMessage() : errorMessage).withTitle("Errore di connessione").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }
}
