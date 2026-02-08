package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import manager.AlertManager;
import manager.PropertiesManager;
import manager.SocketManager;
import model.*;
import model.shared.ClientRequest;
import model.EmailSentList;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;
import task.ReloadInboxTask;
import utility.GenericUtility;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable, ResponseController {

    @FXML
    private BorderPane mainPane;
    @FXML
    private Label lastUpdateLb;
    @FXML
    private Label userMail;
    @FXML
    private Label sideMenuAccountLB;
    @FXML
    private Label notifyLb;
    @FXML
    private HBox notifyHB;

    private EmailList emailList;
    private EmailSentList emailSentList;
    private BozzeList bozzeList;
    private User user;
    private PropertiesManager propertiesManager;

    public void initController(List<Email> initalList, User user, ScheduledExecutorService scheduledExec) {
        this.user = user;
        this.userMail.textProperty().bind(user.mailProperty());
        GenericUtility.setProfileImage(user.getMail(), sideMenuAccountLB);

        this.emailList = new EmailList();
        this.emailSentList = new EmailSentList();
        this.bozzeList = new BozzeList();
        if(initalList == null) {
            new SocketManager(exec, new ClientRequest(TypeOfRequest.GET_INBOX, this.user, null), this);
        } else {
            this.emailList.setEmailList(FXCollections.observableList(initalList));
        }
        try {
            this.propertiesManager = new PropertiesManager("client.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //faccio partire i task schedulati
        if(this.propertiesManager != null)
            scheduledExec.scheduleAtFixedRate(new ReloadInboxTask(this.emailList, this.user, this.lastUpdateLb),
                    Integer.parseInt(this.propertiesManager.getProperty("RELOAD_INBOX_TASK_INITIAL_SEC") != null ? this.propertiesManager.getProperty("RELOAD_INBOX_TASK_INITIAL_SEC") : "0"),
                    Integer.parseInt(this.propertiesManager.getProperty("RELOAD_INBOX_TASK_SEC") != null ? this.propertiesManager.getProperty("RELOAD_INBOX_TASK_SEC") : "10"),
                    TimeUnit.SECONDS);

        notifyLb.textProperty().bind(this.emailList.numberOfNotificationsProperty());
        notifyLb.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(observableValue.getValue().equals("0"))
                    notifyHB.setVisible(false);
                else
                    notifyHB.setVisible(true);
            }
        });

        this.emailList.getEmailListNotVisualized().addListener(new ListChangeListener<Email>() {
            @Override
            public void onChanged(Change<? extends Email> c) {
                emailList.setNumberOfNotifications(emailList.getEmailListNotVisualized().size() + "");
            }
        });
        this.emailList.setLastUpdateLb(this.lastUpdateLb);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.lastUpdateLb.setText("Ultimo aggiornamento: " + df.format(new Date()));
    }

    @FXML
    public void inArrivoMailList() {
        //Mostro la videata della mailInList
        try {
            FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/List.fxml"));
            mainPane.setCenter(listLoader.load());
            ListController listController = listLoader.getController();
            listController.initController(this.emailList, TypeOfData.IN_ARRIVO, user);
        } catch (IOException e) {
            new AlertManager()
                    .withType(Alert.AlertType.ERROR)
                    .withTitle("Errore generico")
                    .withHeaderText("e.getMessage()")
                    .withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void scriviMail() {
        //Mostro la videata per scrivere una nuovo mail
        try {
            FXMLLoader mailboxLoader = new FXMLLoader(getClass().getResource("/WriteMail.fxml"));
            mainPane.setCenter(mailboxLoader.load());
            MailBoxController mailBoxController = mailboxLoader.getController();
            this.emailList.setCurrentEmail(null);
            mailBoxController.initController(this.emailList, this.user, null);
        } catch (IOException e) {
            new AlertManager()
                    .withType(Alert.AlertType.ERROR)
                    .withTitle("Errore generico")
                    .withHeaderText("e.getMessage()")
                    .withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void bozzeMailList() {
        //Mostro la videata della mailInList
        try {
            FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/List.fxml"));
            mainPane.setCenter(listLoader.load());
            ListController bozzeController = listLoader.getController();
            bozzeController.initController(this.bozzeList, TypeOfData.BOZZE, user);
        } catch (IOException e) {
            new AlertManager()
                    .withType(Alert.AlertType.ERROR)
                    .withTitle("Errore generico")
                    .withHeaderText("e.getMessage()")
                    .withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void inviatiMailList() {
        //Mostro la videata della mailInList inviati
        try {
            FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/List.fxml"));
            mainPane.setCenter(listLoader.load());
            ListController listController = listLoader.getController();
            listController.initController(this.emailSentList, TypeOfData.INVIATI, user);
        } catch (IOException e) {
            new AlertManager()
                    .withType(Alert.AlertType.ERROR)
                    .withTitle("Errore generico")
                    .withHeaderText("e.getMessage()")
                    .withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void onLogout() {
        //Devo cancellare il file dell'utente
        File userFile = new File("src/main/java/data/user_" + this.user.getMail().split("@")[0] + ".json");
        if(userFile.exists()) {
            userFile.delete();
        }
        //Porto fuori l'utente
        try {
            FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("/LogIn.fxml"));

            Parent rootLogin = logInLoader.load();
            LogInController logInController = logInLoader.getController();
            logInController.getScheduledExec().shutdownNow();

            Stage loginStage = new Stage();
            loginStage.setTitle("Not A Domain - v 1.0");
            loginStage.setScene(new Scene(rootLogin));
            loginStage.setResizable(false);
            //loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo1.png")));
            loginStage.show();

            //Nascondo la schermata del main
            mainPane.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {
        if(action == TypeOfRequest.GET_INBOX && response.getResultList() != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    emailList.setEmailList(FXCollections.observableList(response.getResultList()));
                }
            });
        }
    }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) { }

    @Override
    public void onSocketError(Exception e, String errorMessage) { }
}
