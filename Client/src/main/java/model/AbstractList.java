package model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class AbstractList {
    protected ObservableList<Email> emailList = FXCollections.observableArrayList
            (email -> new Observable[] {email.idProperty()});

    protected ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(new Email());

    public ObservableList<Email> getEmailList() {
        return emailList;
    }

    public void setEmailList(ObservableList<Email> emailList) {
        this.emailList = emailList;
    }

    public Email getCurrentEmail() {
        return currentEmail.get();
    }

    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail;
    }

    public void setCurrentEmail(Email currentEmail) {
        this.currentEmail.set(currentEmail);
    }

    public void setCurrentEmailProperty(ObjectProperty<Email> property) { this.currentEmail = property; }

    public abstract void loadData(Object input);
}
