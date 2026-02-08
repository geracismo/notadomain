package model.shared;

public enum TypeOfRequest {
    GET_INBOX, //richiesta di mail in arrivo
    GET_SEND, //richiesta di mail inviate
    SEND_EMAIL, //richiesta di invio mail
    DELETE_EMAIL, //richiesta di eliminazione email
    GET_NOTIFICATION_INBOX, //richiesta di mail in arrivo non ancora visulaizzate
    OPEN_EMAIL // richiesta di apertura email
}
