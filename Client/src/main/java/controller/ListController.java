package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import manager.AlertManager;
import manager.SocketManager;
import model.*;
import model.shared.ClientRequest;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;
import utility.EmailUtility;
import utility.GenericUtility;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ListController implements Initializable, ResponseController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox mailBox;
    @FXML
    private ListView<Email> listView;
    @FXML
    private Label typeOfListLb;
    @FXML
    private ImageView reloadImg;
    @FXML
    private Label accountLb;
    @FXML
    private Label mittenteLb;
    @FXML
    private Label oggettoLb;
    @FXML
    private Label destLb;
    @FXML
    private Label copyLb;
    @FXML
    private Label bodyLb;
    @FXML
    private Label dataLb;
    @FXML
    private ImageView rispondiAllBtn;
    @FXML
    private ImageView rispondiBtn;
    @FXML
    private ImageView inoltraBtn;
    @FXML
    private ImageView deleteEmailBtn;


    private TypeOfData typeOfList;
    private AbstractList emailList;
    private User user;
    //Viene usata per evitare che lo showmail (durante lo schedulatore) si esegua quando si sta rispondendo
    private static boolean isInWriting = false;

    private final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yy");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //suggerimenti
        Tooltip.install(rispondiAllBtn, new Tooltip("Rispondi a tutti"));
        Tooltip.install(rispondiBtn, new Tooltip("Rispondi"));
        Tooltip.install(inoltraBtn, new Tooltip("Inoltra"));
        Tooltip.install(deleteEmailBtn, new Tooltip("Elimina"));
        Tooltip.install(reloadImg, new Tooltip("Aggiorna"));

        this.typeOfListLb.setText("");
        this.mailBox.setVisible(false);
        this.reloadImg.setVisible(false);
        ListController.isInWriting = false;
    }

    public void initController(AbstractList sharedList, TypeOfData typeOfList, User user) {
        this.user = user;
        mittenteLb.setText(user.getMail());

        if(sharedList == null)
            return;

        this.emailList = sharedList;
        this.emailList.setCurrentEmail(null);

        //Carico i dati in base al tipo di lista corrente
        if(typeOfList == TypeOfData.BOZZE) {
            this.emailList.loadData(this.user);
        } else if(typeOfList == TypeOfData.IN_ARRIVO) {
            this.reloadImg.setVisible(true);
        } else if(typeOfList == TypeOfData.INVIATI) {
            ClientRequest sendInbox = new ClientRequest(TypeOfRequest.GET_SEND, this.user, null);
            this.emailList.loadData(sendInbox);
        }

        //Rimuovo le vecchie istanze di currentEmail con i vecchi listener associati
        this.emailList.setCurrentEmailProperty(new SimpleObjectProperty<>(new Email()));

        this.typeOfList = typeOfList;

        String typeOfListTitle = typeOfList.name().replaceAll("_", " ").toLowerCase();
        this.typeOfListLb.setText(typeOfListTitle.substring(0, 1).toUpperCase() + typeOfListTitle.substring(1) + ":");

        //Configuration of ListView
        listView.setItems(emailList.getEmailList());
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    emailList.setCurrentEmail(newSelection);
                }
        );

        emailList.currentEmailProperty().addListener((obs, oldEmailSelected, newEmailSelected) -> {
            if(newEmailSelected == null)
                listView.getSelectionModel().clearSelection();
            else {
                listView.getSelectionModel().select(newEmailSelected);
                //Binding di mail
                mittenteLb.textProperty().bind(newEmailSelected.mittProperty());
                copyLb.textProperty().bind(newEmailSelected.ccProperty());
                destLb.textProperty().bind(newEmailSelected.destProperty());
                oggettoLb.textProperty().bind(newEmailSelected.subjectProperty());
                bodyLb.textProperty().bind(newEmailSelected.bodyProperty());

                if (this.typeOfList == TypeOfData.IN_ARRIVO || this.typeOfList == TypeOfData.INVIATI) {
                    if(!isInWriting) {
                        showMail(newEmailSelected);
                        if(this.typeOfList == TypeOfData.IN_ARRIVO) {
                            //Faccio la richiesta al server per segnalare che la mail è stata aperta
                            new SocketManager(exec, new ClientRequest(TypeOfRequest.OPEN_EMAIL, this.user, newEmailSelected), this);
                        }
                    }
                } else if (this.typeOfList == TypeOfData.BOZZE) {
                    showBozze();
                }
            }
        });

        //gestisce la grafica degli elementi della listView
        listView.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            protected void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);
                if(empty)
                    setText(null);
                else {
                    if (typeOfList == TypeOfData.IN_ARRIVO) {
                        if(((EmailList)emailList).isAlreadyVisualizated(item))
                            setStyle("-fx-text-fill: #9600F4");
                        else
                            setStyle("-fx-text-fill: white");
                    }else
                        setStyle("-fx-text-fill: white");

                    setText(item.getMitt() != null
                            ? item.getMitt() + (item.getSubject() != null && !item.getSubject().equals("") ? "\n" + item.getSubject() : "\nnessun oggetto") + (item.getDate() != null ? "\n" + defaultDateFormat.format(item.getDate()) : "")
                            : "Nessun destinatario" + (item.getSubject() != null && !item.getSubject().equals("") ? "\n" + item.getSubject() : "\nnessun oggetto") + (item.getDate() != null ? "\n" + defaultDateFormat.format(item.getDate()) : ""));
                }
            }
        });
    }

    private void showMail(Email emailSelected) {
        //imposto immagine profilo
        GenericUtility.setProfileImage(emailSelected.getMitt(), this.accountLb);

        //imposto la data di arrivo
        if(emailSelected.getDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy\nHH:mm");
            dataLb.setText(df.format(emailSelected.getDate()));
        } else
            dataLb.setText("");

        //rendo visibile la mail
        mailBox.setVisible(true);
    }

    @FXML
    public void deleteEmail() {
        Email emailSelected = this.emailList.getCurrentEmail();
        if(emailSelected == null) { return; }

        //richiesta di eliminazione al server
        ClientRequest deleteRequest = new ClientRequest(TypeOfRequest.DELETE_EMAIL, this.user, emailSelected);
        new SocketManager(exec, deleteRequest, this);
    }

    @FXML
    public void answerMail() {
        try {
            FXMLLoader mailboxLoader = new FXMLLoader(getClass().getResource("/WriteMail.fxml"));
            hideAll();
            mainPane.setTop(mailboxLoader.load());
            MailBoxController controller = mailboxLoader.getController();

            //Non settare il mittente perchè viene fatto in MailBoxController
            Email emailToSend = new Email(emailList.getCurrentEmail());
            emailToSend.setId(Long.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())).intValue() + "");
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
            emailToSend.setBody(emailToSend.getDate() != null
                    ? "\n\n\n----------------------------------------\nIl giorno " + defaultDateFormat.format(emailToSend.getDate()) + " alle ore " + hourFormat.format(emailToSend.getDate()) + ", <" + emailToSend.getMitt() + "> ha scritto:" +
                            "\n" + emailToSend.getBody()
                    : "\n\n\n----------------------------------------\n" + emailToSend.getBody());
            emailToSend.setCc(null);
            emailToSend.setSubject("Re: " + emailToSend.getSubject());
            if(typeOfList == TypeOfData.IN_ARRIVO) {
                emailToSend.setDest(emailToSend.getMitt());
            }
            ListController.isInWriting = true;

            controller.initController(emailList, this.user, emailToSend);
        } catch (IOException e) {
            new AlertManager().withType(Alert.AlertType.ERROR).withContentText(e.getMessage()).withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void answerAllMail() {
        try {
            FXMLLoader mailboxLoader = new FXMLLoader(getClass().getResource("/WriteMail.fxml"));
            hideAll();
            mainPane.setTop(mailboxLoader.load());
            MailBoxController controller = mailboxLoader.getController();

            //Non settare il mittente perchè viene fatto in MailBoxController
            Email emailToSend = new Email(emailList.getCurrentEmail());
            emailToSend.setId(Long.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())).intValue() + "");
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
            emailToSend.setBody(emailToSend.getDate() != null
                    ? "\n\n\n----------------------------------------\nIl giorno " + defaultDateFormat.format(emailToSend.getDate()) + " alle ore " + hourFormat.format(emailToSend.getDate()) + ", <" + emailToSend.getMitt() + "> ha scritto:" +
                    "\n" + emailToSend.getBody()
                    : "\n\n\n----------------------------------------\n" + emailToSend.getBody());
            emailToSend.setSubject("Re: " + emailToSend.getSubject());
            if(typeOfList == TypeOfData.IN_ARRIVO) {
                boolean isInCC = false;
                //Se lo user è tra i CC della mail mi devo comportare diversamente
                if(emailToSend.getCc() != null) {
                    List<String> listOfCC = EmailUtility.getAllRecipients(emailToSend.getCc());
                    for(String cc: listOfCC) {
                        if(cc.trim().equalsIgnoreCase(this.user.getMail())) {
                            isInCC = true; break;
                        }
                    }
                }
                if(!isInCC)
                    emailToSend.setDest(emailToSend.getMitt() + ";");
                else {
                    emailToSend.setDest(emailToSend.getMitt() + "; " + emailToSend.getDest());
                    emailToSend.setCc(emailToSend.getCc().replaceAll(this.user.getMail().trim(), ""));
                }
            }
            controller.initController(emailList, this.user, emailToSend);
            ListController.isInWriting = true;
        } catch (IOException e) {
            new AlertManager().withType(Alert.AlertType.ERROR).withContentText(e.getMessage()).withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void forwardMail() {
        try {
            FXMLLoader mailboxLoader = new FXMLLoader(getClass().getResource("/WriteMail.fxml"));
            hideAll();
            mainPane.setTop(mailboxLoader.load());
            MailBoxController controller = mailboxLoader.getController();

            //Non settare il mittente perchè viene fatto in MailBoxController
            Email emailToSend = new Email(emailList.getCurrentEmail());
            emailToSend.setId(emailToSend.generateRandomId());
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
            emailToSend.setBody(emailToSend.getDate() != null
                    ? "\n\n\n----------------------------------------\nIl giorno " + defaultDateFormat.format(emailToSend.getDate()) + " alle ore " + hourFormat.format(emailToSend.getDate()) + ", <" + emailToSend.getMitt() + "> ha scritto:" +
                    "\n" + emailToSend.getBody()
                    : "\n\n\n----------------------------------------\n" + emailToSend.getBody());
            emailToSend.setDest(null);
            emailToSend.setCc(null);

            controller.initController(emailList, this.user, emailToSend);
            ListController.isInWriting = true;
        } catch (IOException e) {
            new AlertManager().withType(Alert.AlertType.ERROR).withContentText(e.getMessage()).withButton(ButtonType.OK).show();
        }
    }

    private void showBozze() {
        try {
            FXMLLoader mailboxLoader = new FXMLLoader(getClass().getResource("/WriteMail.fxml"));
            mainPane.setCenter(mailboxLoader.load());
            MailBoxController mailboxcontroller = mailboxLoader.getController();

            Email bozza = new Email(this.emailList.getCurrentEmail());
            mailboxcontroller.initController(this.emailList, this.user, bozza, this.typeOfList);
            mailboxcontroller.resizeMailBox(483);
            mailboxcontroller.setActionBarColor("#282829");

        } catch (IOException e) {
            new AlertManager().withType(Alert.AlertType.ERROR).withContentText(e.getMessage()).withButton(ButtonType.OK).show();
        }
    }

    @FXML
    public void onReloadList() {
        //Ricarico la lista degli in arrivo
        this.emailList.loadData(new ClientRequest(TypeOfRequest.GET_NOTIFICATION_INBOX, user, null));
    }

    private void hideAll() {
        listView.setVisible(false);
        mailBox.setVisible(false);
        typeOfListLb.setVisible(false);
        reloadImg.setVisible(false);
    }


    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {
        if(action == TypeOfRequest.DELETE_EMAIL) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    emailList.getEmailList().remove(emailList.getCurrentEmail());
                    emailList.setCurrentEmail(null);
                    mailBox.setVisible(false);
                }
            });
        } else if(action == TypeOfRequest.OPEN_EMAIL && response.getResultList() != null && !response.getResultList().isEmpty()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ((EmailList) emailList).getEmailListNotVisualized().removeIf(e -> e.getId().equals(response.getResultList().get(0).getId()));
                    listView.refresh();
                }
            });
        }
    }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText(response.getDescription()).withTitle("Warning").withType(Alert.AlertType.WARNING).showInAnotherThread();
    }

    @Override
    public void onSocketError(Exception e, String errorMessage) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText(errorMessage == null ? e.getMessage() : errorMessage).withTitle("Errore di connessione").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }

}
