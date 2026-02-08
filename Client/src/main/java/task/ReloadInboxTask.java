package task;

import controller.ResponseController;
import javafx.application.Platform;
import javafx.scene.control.Label;
import model.EmailList;
import model.User;
import model.shared.ClientRequest;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ReloadInboxTask implements Runnable, ResponseController {

    private final Label lastUpdateLb;
    private final EmailList emailList;
    private final User user;

    public ReloadInboxTask(EmailList emailList, User user, Label lastUpdateLb) {
        this.emailList = emailList;
        this.user = user;
        this.lastUpdateLb = lastUpdateLb;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                emailList.loadData(new ClientRequest(TypeOfRequest.GET_NOTIFICATION_INBOX, user, null));

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                lastUpdateLb.setText("Ultimo aggiornamento: " + df.format(new Date()));
            }
        });
    }

    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) { }

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) { }

    @Override
    public void onSocketError(Exception e, String errorMessage) { }
}
