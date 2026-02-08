package model;

import adapter.Adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

public class BozzeList extends AbstractList {

    @Override
    public void loadData(Object input) {
        this.emailList.clear();
        User user = (User) input;
        File fileBozze = new File("src/main/java/data/bozze_" + user.getMail().split("@")[0] + ".json");

        //Se il file non esiste non carico nulla
        if(!fileBozze.exists())
            return;

        try {
            //Adding bozze.json
            Email[] listOfBozze = Adapter.getObjectFromFile(new FileReader(fileBozze), Email[].class);
            if(listOfBozze != null)
                this.emailList.addAll(Arrays.asList(listOfBozze));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
