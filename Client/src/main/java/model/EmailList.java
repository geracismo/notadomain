package model;

import controller.ResponseController;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import manager.AlertManager;
import manager.SocketManager;
import model.shared.ClientRequest;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class EmailList extends AbstractList implements ResponseController {

    private ObservableList<Email> emailListNotVisualized = FXCollections.observableArrayList
            (email -> new Observable[] {email.idProperty()});

    private StringProperty numberOfNotifications = new SimpleStringProperty();

    private Label lastUpdateLb;

    public ObservableList<Email> getEmailListNotVisualized() { return emailListNotVisualized; }

    public void setEmailListNotVisualized(ObservableList<Email> emailListNotVisualized) { this.emailListNotVisualized = emailListNotVisualized; }

    public String getNumberOfNotifications() {
        return numberOfNotifications.get();
    }

    public StringProperty numberOfNotificationsProperty() {
        return numberOfNotifications;
    }

    public void setNumberOfNotifications(String numberOfNotifications) {
        this.numberOfNotifications.set(numberOfNotifications);
    }

    public void setLastUpdateLb(Label lastUpdateLb) {
        this.lastUpdateLb = lastUpdateLb;
    }

    //metodo per caricare la listView
    @Override
    public void loadData(Object input) {
        this.emailListNotVisualized.clear();
        new SocketManager(exec, (ClientRequest) input, this);
    }

    public boolean isAlreadyVisualizated(Email e) {
        for(Email s : this.emailListNotVisualized) {
            if (e.equals(s))
                return true;
        }
        return false;
    }

    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {
        if(response.getResultList() != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    emailListNotVisualized.addAll(response.getResultList());
                    //Se ci sono alcune email che non esistono ancora nella lista principale (emailList) allora le devo inserire
                    //Set --> non contiene duplicati senza ordine   TreeSet --> non contiene duplicati e ha un criterio d'ordine
                    Set<Email> finalEmailList = new TreeSet<>(new SortByDate());

                    //aggiungo le mail gia' presenti
                    finalEmailList.addAll(emailList);

                    //aggiungo le mail nuove non visualizzata (GET_NOTIFICATION_INBOX)
                    finalEmailList.addAll(response.getResultList());
                    Email actualCurrentEmail = getCurrentEmail();

                    if(finalEmailList.size() > 0) {
                        emailList.clear();
                        //Collections.sort(finalEmailList, new SortByDate());
                        for(Email e : finalEmailList) {
                            emailList.add(0, e);
                        }
                        setCurrentEmail(actualCurrentEmail);
                    }
                }
            });
        } else
            new AlertManager().withType(Alert.AlertType.WARNING).withTitle("In arrivo").withHeaderText(response.getDescription()).withButton(ButtonType.OK).showInAnotherThread();
        if(this.lastUpdateLb != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    lastUpdateLb.setText("Ultimo aggiornamento: " + df.format(new Date()));
                }
            });
        }
    }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText("Errore durante il caricamento della posta in arrivo:\n" + response.getDescription()).withTitle("In arrivo").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }

    @Override
    public void onSocketError(Exception e, String errorMessage) {
        //new AlertManager().withButton(ButtonType.OK).withHeaderText(errorMessage == null ? e.getMessage() : errorMessage).withTitle("Errore di connessione").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }
}

class SortByDate implements Comparator<Email> {

    @Override
    public int compare(Email o1, Email o2) {
        if(o1.getDate() == null)
            return -1;
        else if(o2.getDate() == null)
            return 1;
        return o1.getDate().compareTo(o2.getDate());
    }
}
