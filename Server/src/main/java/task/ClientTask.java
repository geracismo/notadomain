package task;

import engine.*;
import manager.LoggerManager;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTask implements Runnable {

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private LoggerManager logger;

    public ClientTask(Socket client, LoggerManager logger) throws IOException {
        this.client = client;
        this.in = new ObjectInputStream(client.getInputStream());
        this.out = new ObjectOutputStream(client.getOutputStream());
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            //1. Acquisisco la richiesta del client
            ClientRequest request = (ClientRequest) this.in.readObject();
            this.logger.info("Recived request " + request.getAction().name() + " from " + this.client.getInetAddress().getCanonicalHostName());
            if(request.getAction() == TypeOfRequest.SEND_EMAIL)
                this.logger.info("<" + request.getEmail().getMitt() + "> send mail to <" + request.getEmail().getDest() + ">");
            ServerResponse response;
            //2. Elaboro la richiesta
            AbstractEngine engine = getEngine(request.getAction());
            if(engine != null) {
                try {
                    response = engine.elaborate(request);
                } catch (Exception e) {
                    response = new ServerResponse(ResponseStatus.ERROR, e.getMessage(), null);
                    this.logger.error("An error occurred during request elaboration",e);
                }
            } else {
                response = new ServerResponse(ResponseStatus.ACTION_UNKNOWN, ResponseStatus.ACTION_UNKNOWN.description, null);
            }
            //3.Invio la risposta
            out.writeObject(response);
        } catch (ClassNotFoundException | IOException e) {
            this.logger.error("Client connection error:", e);
        } finally {
            this.logger.info("End of request of socket " + this.client.getInetAddress().getCanonicalHostName());
            //Chiudo il collegamento
            try {
                this.client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private AbstractEngine getEngine(TypeOfRequest type) {
        if (type == TypeOfRequest.GET_INBOX)
            return new InboxEngine();
        if (type == TypeOfRequest.GET_SEND)
            return new SendInboxEngine();
        if (type == TypeOfRequest.SEND_EMAIL)
            return new SendEngine();
        if (type == TypeOfRequest.DELETE_EMAIL)
            return new DeleteEngine();
        if (type == TypeOfRequest.GET_NOTIFICATION_INBOX)
            return new NotificationInboxEngine();
        if (type == TypeOfRequest.OPEN_EMAIL)
            return new OpenMailEngine();
        return null;
    }
}
