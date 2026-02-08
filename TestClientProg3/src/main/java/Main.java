import model.Email;
import model.User;
import model.shared.ClientRequest;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        User user1 = new User();
        user1.setName("Ettore");
        user1.setSurname("Calvi");
        user1.setMail("ettore.calvi@edu.unito.it");

        User user2 = new User();
        user2.setName("Simone");
        user2.setSurname("Geraci");
        user2.setMail("simone.geraci@edu.unito.it");

        User user3 = new User();
        user3.setName("Lorenzo");
        user3.setSurname("Bergadano");
        user3.setMail("lorenzo.bergadano@edu.unito.it");

        User user4 = new User();
        user4.setName("Cosimo");
        user4.setSurname("Mignottaro");
        user4.setMail("cosimo.mignottaro@edu.unito.it");

        Socket s = null;
        try {
            s = new Socket("localhost",
                    1999);
            System.out.println("Collegato");
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            System.out.println("Sto per inviare la richiesta al server ... Premere un tasto");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            //Log di test:
            //1. 1 utente invia ad 1 dest OK
            //2. 1 utente invia ad 1 dest, 1 cc OK
            //3. 1 utente invia ad 2 dest OK
            //4. 1 utente invia ad 1 dest e 2 cc OK
            //5. 1 utente invia ad 1 dest 1 cc errato OK

            Email emailToSend = new Email(user2.getMail(), user1.getMail(), user3.getMail() + ";" + user4.getMail(),
                    "Oggetto prova Cc", "Corpo prova Cc", new Date());
            emailToSend.setId("b8c4d31b-0d9a-40be-99df-b00b7a53e93e");

            //ClientRequest request = new ClientRequest(TypeOfRequest.SEND_EMAIL, null, emailToSend);
            //ClientRequest request = new ClientRequest(TypeOfRequest.GET_INBOX, user1, null);
            //ClientRequest request = new ClientRequest(TypeOfRequest.GET_SEND, user2, null);
            //ClientRequest request = new ClientRequest(TypeOfRequest.DELETE_EMAIL, user1, emailToSend);
            ClientRequest request = new ClientRequest(TypeOfRequest.GET_NOTIFICATION_INBOX, user1, null);
            //ClientRequest request = new ClientRequest(TypeOfRequest.OPEN_EMAIL, null, emailToSend);

            out.writeObject(request);
            System.out.println("Inviato object");

            ServerResponse response = (ServerResponse) in.readObject();
            System.out.println("Ricevuto " + response.getStatus().name());
            if(response.getResultList() != null)
                for(Email e : response.getResultList())
                    System.out.println(e);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
