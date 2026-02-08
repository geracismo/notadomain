package engine;

import adapter.Adapter;
import model.Email;
import model.shared.ClientRequest;
import model.shared.ServerResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.*;

public abstract class AbstractEngine {
    private final String DATA_PATH = "src/main/java/data/";
    private static Map<String, ReadWriteLock> locks = new HashMap<>();

    public abstract ServerResponse elaborate(ClientRequest request) throws Exception;

    protected List<String> getAllRecipients(String destinatari) {
        List<String> result = new ArrayList<>();

        if(destinatari.contains(";"))
            result.addAll(Arrays.asList(destinatari.split(";")));
        else
            result.add(destinatari);

        return result;
    }

    protected File getDataFile(String email) {
        File result = new File(DATA_PATH + email.split("@")[0] + ".json");
        if(result.exists() && locks.get(result.getName()) == null) {
            locks.put(result.getName(), new ReentrantReadWriteLock());
        }
        return result;
    }

    protected void writeFile(File fileToWrite, Object o) throws IOException {
        ReadWriteLock fileLock = locks.get(fileToWrite.getName());
        fileLock.writeLock().lock();
        try {
            Adapter.writeObjectToFile(fileToWrite, o);
        } catch (IOException e) {
            throw e;
        } finally {
            fileLock.writeLock().unlock();
        }
    }

    protected Email[] readFile(File fileToRead) throws FileNotFoundException {
        ReadWriteLock fileLock = locks.get(fileToRead.getName());
        if(fileLock == null)
            throw new FileNotFoundException("Il file " + fileToRead.getName() + " non esiste");
        fileLock.readLock().lock();
        Email[] result = null;
        try {
            result = Adapter.getObjectFromFile(new FileReader(fileToRead), Email[].class);
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            fileLock.readLock().unlock();
        }
        return result;
    }

}