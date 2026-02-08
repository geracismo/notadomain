package model;

import controller.ResponseController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import manager.AlertManager;
import manager.SocketManager;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;

public class EmailSentList extends AbstractList implements ResponseController {

    //metodo per caricare la listView
    @Override
    public void loadData(Object input) {
        this.emailList.clear();
        new SocketManager(exec, (ClientRequest) input, this);
    }

    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {
        if((response.getStatus() == ResponseStatus.OK || response.getStatus() == ResponseStatus.IS_EMPTY) && response.getResultList() != null) {

            for(Email e : response.getResultList())
                emailList.add(0, e);

            if (response.getResultList().size() == 0)
                new AlertManager().withButton(ButtonType.OK).withTitle("Inviati").withHeaderText("La lista di mail inviate é vuota").withType(Alert.AlertType.INFORMATION).showInAnotherThread();
        }  else
            new AlertManager().withType(Alert.AlertType.WARNING).withTitle("Impossibile recuperare le mail.").withHeaderText(response.getDescription()).withButton(ButtonType.OK).showInAnotherThread();
    }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText("Errore durante il caricamento delle mail inviate:\n" + response.getDescription()).withTitle("Inviati").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }

    @Override
    public void onSocketError(Exception e, String errorMessage) {
        new AlertManager().withButton(ButtonType.OK).withHeaderText(errorMessage == null ? e.getMessage() : errorMessage).withTitle("Errore di connessione").withType(Alert.AlertType.ERROR).showInAnotherThread();
    }
}
