package engine;

import model.Email;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class NotificationInboxEngine extends AbstractEngine{

    @Override
    public ServerResponse elaborate(ClientRequest request) throws Exception {
        //L'engine è simile al InboxEngine ma torna solo le Email non lette
        ServerResponse response;
        //1. Carico il file json dell'utente
        File userFile = getDataFile(request.getUser().getMail().toLowerCase());

        //Se il file non esiste questo engine non deve avere i permessi per poterlo creare
        if(!userFile.exists()) {
            return new ServerResponse(ResponseStatus.USER_NOT_EXIST, ResponseStatus.USER_NOT_EXIST.description, null);
        }

        //In qualsiasi caso il file qui esiste. Se è vuoto setta .IS_EMPTY altrimenti popola la lista
        try {
            Email[] inboxList = readFile(userFile);
            List<Email> outputInbox = new ArrayList<>();
            if(inboxList == null || inboxList.length == 0){
                //2. Popolo la ServerResponse
                //La Inbox è vuota, creo la server response opportuna
                response = new ServerResponse(ResponseStatus.IS_EMPTY, ResponseStatus.IS_EMPTY.description, new ArrayList<Email>());
            } else {
                for(Email e : inboxList) {
                    //Nota: e.getDest contiene una lista di destinatari
                    List<String> destinatari = getAllRecipients(e.getDest());
                    for(String destinatario : destinatari) {
                        if(destinatario.equalsIgnoreCase(request.getUser().getMail().trim()) && !e.isAlreadyReaded())
                            outputInbox.add(e);
                    }
                    //Se non ho trovato ancora nulla dai destinatari, cero nei CC
                    if(outputInbox.size() == 0 && e.getCc() != null) {
                        List<String> destinatariCC = getAllRecipients(e.getCc());
                        for(String destinatario : destinatariCC) {
                            if(destinatario.equalsIgnoreCase(request.getUser().getMail().trim()) && !e.isAlreadyReaded())
                                outputInbox.add(e);
                        }
                    }
                }
                if(outputInbox.size() == 0)
                    response = new ServerResponse(ResponseStatus.IS_EMPTY, ResponseStatus.IS_EMPTY.description, new ArrayList<Email>());
                else
                    response = new ServerResponse(ResponseStatus.OK, ResponseStatus.OK.description, outputInbox);
            }
        } catch (FileNotFoundException e) {
            throw e;
        }
        return response;
    }
}
