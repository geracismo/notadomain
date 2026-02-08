package task;

import model.shared.ClientRequest;
import model.shared.ServerResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class RequestTask implements Callable<ServerResponse> {

    private final Socket socket;
    private final ClientRequest request;

    public RequestTask(ClientRequest request, Socket socket) {
        this.request = request;
        this.socket = socket;
    }

    @Override
    public ServerResponse call() throws Exception {
        //Eseguo la richiesta al server
        ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());

        //Invio la richiesta al server
        out.writeObject(this.request);

        //Ricevo la risposta dal server
        return (ServerResponse) in.readObject();
    }
}
