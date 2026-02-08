package manager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerManager {

    private TextArea logTxt;

    private static SimpleDateFormat defaultDateFormat= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    public LoggerManager(TextArea logtxt) {
        this.logTxt = logtxt;
    }

    public void info(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTxt.appendText("[" + defaultDateFormat.format(new Date()) + "] INFO: " + text + "\n");
            }
        });
    }

    public void error(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTxt.appendText("[" + defaultDateFormat.format(new Date()) + "] ERROR: " + text + "\n");
            }
        });
    }

    public void error(Exception e){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTxt.appendText("[" + defaultDateFormat.format(new Date()) + "] ERROR: " + e.getMessage() + "\n");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logTxt.appendText(sw.toString() + "\n");
            }
        });
    }

    public void error(String text, Exception e){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTxt.appendText("[" + defaultDateFormat.format(new Date()) + "] ERROR: " + text + "\n");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logTxt.appendText(sw.toString() + "\n");
            }
        });

    }

    public boolean saveLogFile(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        File logfile = new File("src/main/java/data/log/log_" + df.format(new Date()) + ".log");
        if(!logfile.exists()){
            try {
                logfile.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfile, true)));
            out.println(logTxt.getText());
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
