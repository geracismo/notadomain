package model.shared;

public enum ResponseStatus {
    OK("Richiesta riuscita"),
    IS_EMPTY("Richiesta riuscita, è vuota"),
    ACTION_UNKNOWN("Il server non è in grado di riconoscere il tipo di azione passata"),
    RECIPIENTS_UNKNOWN("I destinatari specificati sono sconosciuti"),
    ERROR("Errore generico"),
    USER_NOT_EXIST("L'user specificato non esiste");

    public String description;
    ResponseStatus(String descr) {
        this.description = descr;
    }
}
