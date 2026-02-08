package manager;

import controller.ResponseController;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;
import task.RequestTask;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class SocketManager implements Runnable {

    private final ExecutorService exec;
    private final ClientRequest request;
    private final ResponseController rq;
    private final int awaitTerm;
    private final TimeUnit unit;

    public SocketManager(ExecutorService exec, ClientRequest request, ResponseController rq) {
        this.exec = exec;
        this.request = request;
        this.rq = rq;
        this.awaitTerm = 5;
        this.unit = TimeUnit.SECONDS;
        begin();
    }

    public SocketManager(ExecutorService exec, ClientRequest request, int awaitTerm, TimeUnit unit, ResponseController rq) {
        this.exec = exec;
        this.request = request;
        this.rq = rq;
        this.awaitTerm = awaitTerm;
        this.unit = unit;
        begin();
    }

    private void begin() { new Thread(this).start(); }

    @Override
    public void run() {
        Socket s = null;
        try {
            s = new Socket("localhost", 1999);

            FutureTask<ServerResponse> ft = new FutureTask<>(new RequestTask(request, s));
            this.exec.submit(ft);

            //attende il ritorno di ft.get al massimo per this.awaitTerm unita' di tempo
            ServerResponse response = ft.get(this.awaitTerm, this.unit);
            if (response.getStatus() == ResponseStatus.OK || response.getStatus() == ResponseStatus.IS_EMPTY)
                this.rq.onSuccess(request.getAction(), response);
            else
                this.rq.onError(request.getAction(), response);

        } catch (ConnectException e) {
            this.rq.onSocketError(e, "Server non raggiungibile.");
        } catch (UnknownHostException e) {
            this.rq.onSocketError(e, "L'indirizzo IP o la porta del server sono errati.");
            e.printStackTrace();
        } catch (IOException e) {
            this.rq.onSocketError(e, null);
            e.printStackTrace();
        } catch (InterruptedException | ExecutionException e) {
            this.rq.onSocketError(e, "Errore di IO verso il server.");
            e.printStackTrace();
        } catch (TimeoutException e) {
            this.rq.onSocketError(e, "Timeout scattato nella richiesta al server.");
            e.printStackTrace();
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}
