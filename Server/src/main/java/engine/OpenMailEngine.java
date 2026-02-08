package engine;

import model.Email;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class OpenMailEngine extends AbstractEngine{

    @Override
    public ServerResponse elaborate(ClientRequest request) throws Exception {
        //Apre il file del mittente e dichiara già letta la mail passata
        ServerResponse response;

        //1. Carico il file json dell'utente che vuole inviare il mittente
        File userFile = getDataFile(request.getUser().getMail().toLowerCase());
        //Se il file non esiste questo engine non deve avere i permessi per poterlo creare
        if(!userFile.exists()) {
            return new ServerResponse(ResponseStatus.USER_NOT_EXIST, ResponseStatus.USER_NOT_EXIST.description, null);
        }

        try {
            Email[] deleteList = readFile(userFile);
            List<Email> outputList = new ArrayList<>();
            List<Email> singleEmailFound = new ArrayList<>();
            if(deleteList == null || deleteList.length == 0) {
                response = new ServerResponse(ResponseStatus.IS_EMPTY, ResponseStatus.IS_EMPTY.description, null);
            } else {
                for(Email e : deleteList){
                    //2. Scorro la l'array di Email e lo copio in una lista. Se l'id dell'oggetto corrente
                    //è uguale a quello della richiesta, modifico il flag
                    if (e.getId().equals(request.getEmail().getId().trim())) {
                        e.setAlreadyReaded(true);
                        singleEmailFound.add(e);
                    }

                    outputList.add(e);
                }
                writeFile(userFile, outputList);
                response = new ServerResponse(ResponseStatus.OK, ResponseStatus.OK.description, singleEmailFound);
            }
        } catch (FileNotFoundException e) {
            throw e;
        }
        return response;

    }
}
