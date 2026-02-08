package model.shared;

import model.Email;

import java.io.Serializable;
import java.util.List;

public class ServerResponse implements Serializable {
    private ResponseStatus status;
    private String description;
    private List<Email> resultList;

    public ServerResponse(ResponseStatus status, String description, List<Email> resultList) {
        this.status = status;
        this.description = description;
        this.resultList = resultList;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Email> getResultList() {
        return resultList;
    }

    public void setResultList(List<Email> resultList) {
        this.resultList = resultList;
    }
}
