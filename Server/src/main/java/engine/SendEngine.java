package engine;

import adapter.Adapter;
import model.Email;
import model.shared.ClientRequest;
import model.shared.ResponseStatus;
import model.shared.ServerResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SendEngine extends AbstractEngine {

    @Override
    public ServerResponse elaborate(ClientRequest request) throws Exception {

        ServerResponse response;
        List<String> destinatari = getAllRecipients(request.getEmail().getDest());
        boolean allDestExists = true;
        List<Email> unknownRecipients = new ArrayList<>();

        //1. Carico il file json dell'utente che vuole inviare il mittente
        File userFile = getDataFile(request.getEmail().getMitt().toLowerCase());
        //Se il file non esiste questo engine non deve avere i permessi per poterlo creare
        if(!userFile.exists()) {
            return new ServerResponse(ResponseStatus.USER_NOT_EXIST, ResponseStatus.USER_NOT_EXIST.description, null);
        }

        //2. invio nella casella di posta dei destinatari la mail
        for(String destinatario : destinatari) {
            File destFile = getDataFile(destinatario);
            //Se il file non esiste questo engine non deve avere i permessi per poterlo creare
            if(!destFile.exists())
                allDestExists = false;

            //invio nella casella di posta dei destinatari la mail
            try {
                Email[] dest = readFile(destFile);
                List<Email> rewriteBox = new ArrayList<>();

                if (dest != null)
                    for(Email e : dest)
                        rewriteBox.add(e);

                rewriteBox.add(request.getEmail());

                writeFile(destFile, rewriteBox);
            } catch (FileNotFoundException e) {
                //Qui non rilanciamo l'eccezione perchè magari non tutti i destinatari sono inesistenti
                Email emailUnknown = new Email(request.getEmail());
                emailUnknown.setDest(destinatario);
                unknownRecipients.add(emailUnknown);
            }
        }

        //3. invio nella casella di posta dei Cc la mail
        boolean allCopyCarbonExists = true;
        if(request.getEmail().getCc() != null && !request.getEmail().getCc().trim().equals("")) {
            List<String> allCopyCarbon = getAllRecipients(request.getEmail().getCc());
            for (String copyCarbon : allCopyCarbon) {
                File allCopyCarbonFile = getDataFile(copyCarbon);

                //Se il file utente del Cc non esiste, questo engine non deve avere i permessi per poterlo creare
                if (!allCopyCarbonFile.exists())
                    allCopyCarbonExists = false;

                //invio nella casella di posta dei Cc la mail
                try {
                    Email[] cc = readFile(allCopyCarbonFile);
                    List<Email> rewriteBox = new ArrayList<>();

                    if (cc != null)
                        for (Email e : cc)
                            rewriteBox.add(e);

                    rewriteBox.add(request.getEmail());

                    writeFile(allCopyCarbonFile, rewriteBox);
                } catch (FileNotFoundException e) {
                    Email emailUnknown = new Email(request.getEmail());
                    emailUnknown.setDest(copyCarbon);
                    unknownRecipients.add(emailUnknown);
                }
            }
        }



        //4. invio nella casella di posta del mittente la mail
        try {
            Email[] dest = readFile(userFile);
            List<Email> rewriteBox = new ArrayList<>();

            if(dest != null)
                for(Email e : dest)
                    rewriteBox.add(e);

            rewriteBox.add(request.getEmail());

            writeFile(userFile, rewriteBox);
        } catch (FileNotFoundException e) {
            throw e;
        }

        if(allDestExists && allCopyCarbonExists)
            response = new ServerResponse(ResponseStatus.OK, ResponseStatus.OK.description, null);
        else
            response = new ServerResponse(ResponseStatus.RECIPIENTS_UNKNOWN, ResponseStatus.RECIPIENTS_UNKNOWN.description, unknownRecipients);
        return response;
    }
}
