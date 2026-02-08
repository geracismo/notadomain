package controller;

import adapter.Adapter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import manager.AlertManager;
import manager.SocketManager;
import model.User;
import model.shared.ClientRequest;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;
import utility.EmailUtility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LogInController implements Initializable, ResponseController {

    @FXML
    private TextField mailField;
    @FXML
    private TextField pswField;
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox vBox;
    @FXML
    private Label errorLb;

    private User user;
    private static ScheduledExecutorService scheduledExec;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLb.setVisible(false);
    }

    @FXML
    public void onSignIn() {
        this.errorLb.setVisible(false);
        String mail = mailField.getText();
        String psw = pswField.getText();

        if (!EmailUtility.isValidMail(mail)) {
            this.errorLb.setText("Inserisci una mail valida.");
            this.errorLb.setVisible(true);

            return;
        }else if(((psw == null) || (psw.equals("")))) {
            this.errorLb.setText("Password mancante.");
            this.errorLb.setVisible(true);

            return;
        }

        this.user = new User(mail, psw);
        new SocketManager(exec, new ClientRequest(TypeOfRequest.GET_INBOX, this.user, null), this);
    }

    @FXML
    public void onHelp() {
        AlertManager help = new AlertManager()
                .withType(Alert.AlertType.INFORMATION)
                .withHeaderText("Not A Domain è un mail client ideato e progettato dagli studenti\nLorenzo Bergadano,  Simone Geraci,  Ettore Calvi")
                .withContentText("Alcuni account di test:\n\nMail: lorenzo.bergadano@nad.com\nMail: simone.geraci@nad.com\nMail: ettore.calvi@nad.com\nMail: liliana.ardissono@nad.com\n\nTutte le password sono finte (si può mettere qualsiasi cosa)");
        help.showAndWait();
    }

    @FXML
    public void onPushEnter() {
        //OnClick enter nelle textfield
        this.onSignIn();
    }


    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {
        vBox.setVisible(false);

        try {
            //Creo il file user
            File userFile = new File("src/main/java/data/user_" + this.user.getMail().split("@")[0] + ".json");
            if(!userFile.exists()) {
                if (!userFile.createNewFile()) {
                    new AlertManager()
                            .withType(Alert.AlertType.ERROR)
                            .withTitle("Log in error")
                            .withHeaderText("Impossibile creare il file utente")
                            .withButton(ButtonType.OK).showInAnotherThread();
                    return;
                }
            }

            PrintWriter writer = new PrintWriter(new FileWriter(userFile));
            writer.println(Adapter.getJson(user));
            writer.close();
        } catch (IOException e) {
                new AlertManager()
                    .withType(Alert.AlertType.ERROR)
                    .withTitle("Log in error")
                    .withHeaderText("Impossibile creare il file utente")
                    .withButton(ButtonType.OK).showInAnotherThread();
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/MainClient.fxml"));
                    Parent root = mainLoader.load();
                    MainController mainController = mainLoader.getController();
                    scheduledExec = Executors.newSingleThreadScheduledExecutor();
                    //all'avvio del main controller facciamo la prima richiesta di GET_INBOX per caricare la lista delle mail inviate
                    mainController.initController(response.getResultList(), user, scheduledExec);

                    Stage mainStage = new Stage();
                    mainStage.setTitle("Not A Domain - v 1.0");
                    mainStage.setScene(new Scene(root));
                    mainStage.setResizable(false);
                    //mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo1.png")));
                    mainStage.show();

                    //Nascondo la schermata di login
                    mainPane.getScene().getWindow().hide();
                } catch (IOException e) {
                    new AlertManager()
                            .withType(Alert.AlertType.ERROR)
                            .withTitle("Errore generico")
                            .withHeaderText(e.getMessage())
                            .withButton(ButtonType.OK).show();
                }
            }
        });
    }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                errorLb.setText("Email inesistente.");
                errorLb.setVisible(true);
            }
        });
    }

    @Override
    public void onSocketError(Exception e, String errorMessage) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText(errorMessage == null ? e.getMessage() : errorMessage).withTitle("Errore di connessione").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }

    public ScheduledExecutorService getScheduledExec() {
        return scheduledExec;
    }
}
