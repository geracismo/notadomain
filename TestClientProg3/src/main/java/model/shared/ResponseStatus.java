package model.shared;

public enum ResponseStatus {
    OK("Request succesfull"),
    IS_EMPTY("Request succesfull, is empty"),
    ACTION_UNKNOWN("Server can't known the passed action type"),
    RECIPIENTS_UNKNOWN("The specified recipients are unknown"),
    ERROR("Generic error"),
    USER_NOT_EXIST("The specified user not exist");

    public String description;
    ResponseStatus(String descr) {
        this.description = descr;
    }
}
