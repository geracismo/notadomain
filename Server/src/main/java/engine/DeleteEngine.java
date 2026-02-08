package engine;

import adapter.Adapter;
import model.Email;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DeleteEngine extends AbstractEngine{
    @Override
    public ServerResponse elaborate(ClientRequest request) throws Exception {

        ServerResponse response;
        boolean emailFound = false;
        //1. Carico il file json dell'utente che vuole eliminare il messaggio
        File userFile = getDataFile(request.getUser().getMail().toLowerCase());

        //Se il file non esiste mando una server response, questo engine non ha i permessi per poterlo creare
        if(!userFile.exists()) {
            return new ServerResponse(ResponseStatus.USER_NOT_EXIST, ResponseStatus.USER_NOT_EXIST.description, null);
        }

        try {
            Email[] deleteList = readFile(userFile);
            List<Email> outputDelete = new ArrayList<>();
            if(deleteList == null || deleteList.length == 0) {
                response = new ServerResponse(ResponseStatus.IS_EMPTY, ResponseStatus.IS_EMPTY.description, null);
            } else {
                for(Email e : deleteList){
                    //2. Scorro la l'array di Email e lo copio in una lista. Se l'id dell'oggetto corrente
                    //è uguale a quello della richiesta, rimuovo l'oggetto dalla lista
                    if(!e.getId().equals(request.getEmail().getId().trim()))
                        outputDelete.add(e);
                    if(e.getId().equals(request.getEmail().getId().trim()))
                        emailFound = true;
                }
                writeFile(userFile, outputDelete);
                if(emailFound)
                    response = new ServerResponse(ResponseStatus.OK, ResponseStatus.OK.description, null);
                else
                    response = new ServerResponse(ResponseStatus.IS_EMPTY, ResponseStatus.IS_EMPTY.description, null);
            }
        } catch (FileNotFoundException e) {
            throw e;
        }
        return response;
    }
}
