package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;

public class User implements Externalizable {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty surname = new SimpleStringProperty();
    private StringProperty mail = new SimpleStringProperty();
    private StringProperty psw = new SimpleStringProperty();

    public User(String mail, String psw) {
        this.setMail(mail);
        this.setPsw(psw);
    }

    public User() { }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSurname() {
        return surname.get();
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getMail() {
        return mail.get();
    }

    public StringProperty mailProperty() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail.set(mail);
    }

    public String getPsw() {
        return psw.get();
    }

    public StringProperty pswProperty() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw.set(psw);
    }

    //Questi metodi servono per la serializzazione
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.getName() != null ? this.getName() : "");
        out.writeUTF(this.getSurname() != null ? this.getSurname() : "");
        out.writeUTF(this.getMail());
        out.writeUTF(this.getPsw() != null ? this.getPsw() : "");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setName(in.readUTF());
        this.setSurname(in.readUTF());
        this.setMail(in.readUTF());
        this.setPsw(in.readUTF());
    }
}