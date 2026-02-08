package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.util.Date;
import java.util.UUID;

public class Email implements Externalizable {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty dest = new SimpleStringProperty();
    private StringProperty cc = new SimpleStringProperty();
    private StringProperty mitt = new SimpleStringProperty();
    private StringProperty subject = new SimpleStringProperty();
    private StringProperty body = new SimpleStringProperty();
    private Date date;
    private boolean alreadyReaded;

    public void setId(String id) { this.id.set(id); }

    public String getId() { return id.get(); }

    public StringProperty idProperty() { return id; }

    public String getDest() {
        return dest.get();
    }

    public StringProperty destProperty() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest.set(dest);
    }

    public String getMitt() {
        return mitt.get();
    }

    public StringProperty mittProperty() {
        return mitt;
    }

    public void setMitt(String mitt) {
        this.mitt.set(mitt);
    }

    public String getSubject() { return subject.get(); }

    public StringProperty subjectProperty() { return subject; }

    public void setSubject(String subject) { this.subject.set(subject); }

    public String getBody() {
        return body.get();
    }

    public StringProperty bodyProperty() {
        return body;
    }

    public void setBody(String body) {
        this.body.set(body);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCc() { return cc.get(); }

    public StringProperty ccProperty() { return cc; }

    public void setCc(String cc) { this.cc.set(cc); }

    public boolean isAlreadyReaded() {
        return alreadyReaded;
    }

    public void setAlreadyReaded(boolean alreadyReaded) {
        this.alreadyReaded = alreadyReaded;
    }

    public Email() {
        this.id.set(UUID.randomUUID().toString());
        this.alreadyReaded = false;
    }

    public Email(String dest, String mitt, String cc, String object, String body, Date date) {
        this.id.set(UUID.randomUUID().toString());
        this.setDest(dest);
        this.setMitt(mitt);
        this.setCc(cc);
        this.setSubject(object);
        this.setBody(body);
        this.date = date;
        this.alreadyReaded = false;
    }
    public Email(Email emailCopy){
        this.id.set(emailCopy.getId());
        this.setDest(emailCopy.getDest());
        this.setMitt(emailCopy.getMitt());
        this.setCc(emailCopy.getCc());
        this.setSubject(emailCopy.getSubject());
        this.setBody(emailCopy.getBody());
        this.date = emailCopy.getDate();
        this.alreadyReaded = false;
    }

    public boolean isEmpty() {
        if((this.getSubject() == null || this.getSubject().equals("")) && (this.getBody() == null || this.getBody().equals(""))
                && (this.getCc() == null || this.getCc().equals("")) && (this.getDest() == null || this.getDest().equals("")))
            return true;
        return false;

    }

    @Override
    public String toString() {
        return "id: " + this.getId() + "\n" +
                "mitt: " + this.getMitt() + "\n" +
                "dest: " + this.getDest() + "\n" +
                "cc: " + this.getCc() + "\n" +
                "subject: " + this.getSubject() + "\n" +
                "body: " + this.getBody() + "\n";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.getId());
        out.writeUTF(this.getDest());
        out.writeUTF(this.getCc() != null ? this.getCc() : "");
        out.writeUTF(this.getMitt());
        out.writeUTF(this.getSubject());
        out.writeUTF(this.getBody());
        out.writeObject(this.getDate());
        out.writeBoolean(this.isAlreadyReaded());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readUTF());
        this.setDest(in.readUTF());
        this.setCc(in.readUTF());
        this.setMitt(in.readUTF());
        this.setSubject(in.readUTF());
        this.setBody(in.readUTF());
        this.setDate((Date) in.readObject());
        this.setAlreadyReaded(in.readBoolean());
    }
}
