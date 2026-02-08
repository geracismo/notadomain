package utility;

import adapter.Adapter;
import javafx.scene.control.Label;
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class GenericUtility {

    public static void setProfileImage(String userMail, Label label) {
        String account = "";
        if(userMail.split("@")[0].contains("."))
            account = userMail.split("@")[0].split("\\.")[0].substring(0,1).toUpperCase() +
                    userMail.split("@")[0].split("\\.")[1].substring(0,1).toUpperCase();
        else
            account = userMail.split("@")[0].substring(0, 1).toUpperCase();
        label.setText(account);
    }

    public static User isLogged(){
        File userFile = new File("src/main/java/data/user.json");
        if(!userFile.exists())
            return null;
        try {
            User user = Adapter.getObjectFromFile(new FileReader(userFile), User.class);
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
