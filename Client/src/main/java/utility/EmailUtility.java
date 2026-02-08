package utility;

import adapter.Adapter;
import model.Email;
import model.TypeOfData;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EmailUtility {

    public static boolean isValidMail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static List<String> getAllRecipients(String destinatari) {
        List<String> result = new ArrayList<>();

        if(destinatari.contains(";")) {
            for(String destinatario : destinatari.split(";"))
                result.add(destinatario);
        } else if(!destinatari.isEmpty() && !destinatari.isBlank())
            result.add(destinatari);

        return result;
    }

    public static void addToFile(Email email, User user, TypeOfData type) throws Exception {
        File fileToAdd = new File("src/main/java/data/" + type.filename + "_" + user.getMail().split("@")[0] + ".json");

        //Creo il file se non esiste
        if(!fileToAdd.exists())
            if(!fileToAdd.createNewFile()) {
                throw new IOException("Impossibile creare il file " + type.filename);
            }

        try {
            //Adding bozze.json
            Email[] listOfBozze = Adapter.getObjectFromFile(new FileReader(fileToAdd), Email[].class);
            List<Email> rewriteBozze = new ArrayList<>();
            if(listOfBozze == null || listOfBozze.length == 0) {
                //Il file è vuoto
                rewriteBozze.add(email);
            } else {
                for(Email e : listOfBozze) {
                    rewriteBozze.add(e);
                }
                rewriteBozze.add(0, email);
            }
            PrintWriter writer = new PrintWriter(new FileWriter(fileToAdd));
            writer.println(Adapter.getJson(rewriteBozze));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public static void removeFromFile(Email email, User user, TypeOfData type) throws Exception {
        File fileToDelete = new File("src/main/java/data/" + type.filename + "_" + user.getMail().split("@")[0] + ".json");
        System.out.println(fileToDelete.getName());
        //Se il file non esiste non possiamo eliminare un bel niente
        if(!fileToDelete.exists())
            throw new FileNotFoundException("Il file " + type.filename + " non esiste nella cartella");

        try {
            //Deleting bozze.json
            Email[] listOfBozze = Adapter.getObjectFromFile(new FileReader(fileToDelete), Email[].class);
            List<Email> rewriteBozze = new ArrayList<>();

            for(Email e : listOfBozze) {
                if(!email.getId().equals(e.getId()))
                    rewriteBozze.add(e);
            }

            PrintWriter writer = new PrintWriter(new FileWriter(fileToDelete));
            writer.println(Adapter.getJson(rewriteBozze));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
