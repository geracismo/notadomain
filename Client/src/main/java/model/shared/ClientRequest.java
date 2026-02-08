package model.shared;

import model.Email;
import model.User;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    private TypeOfRequest action;
    private User user;
    private Email email;

    public ClientRequest(TypeOfRequest action, User user, Email email) {
        this.action = action;
        this.user = user;
        this.email = email;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public TypeOfRequest getAction() {
        return action;
    }

    public void setAction(TypeOfRequest action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
