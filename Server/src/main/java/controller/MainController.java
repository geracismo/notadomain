package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import manager.ConnectionManager;
import manager.LoggerManager;
import manager.PropertiesManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {

    @FXML
    private TextArea logTxt;

    private LoggerManager logger;
    private PropertiesManager propertiesManager;
    private ExecutorService exec;
    private ConnectionManager conn;

    private int numThread = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.logger = new LoggerManager(logTxt);
        try {
            this.propertiesManager = new PropertiesManager("server.properties");
            this.numThread = (propertiesManager.getProperty("NUM_THREAD") != null ? Integer.parseInt(propertiesManager.getProperty("NUM_THREAD")) : 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onStart() {
        //Se il server è già partito lo segnalo
        if(this.conn == null || !this.conn.isStarted()) {
            int port = (propertiesManager.getProperty("PORT") != null ? Integer.parseInt(propertiesManager.getProperty("PORT")) : 1999);
            this.logger.info("Starting server on port " + port + "...");

            this.exec = Executors.newFixedThreadPool(this.numThread);
            this.conn = new ConnectionManager(this.exec, port, this.logger);
        } else {
            this.logger.info("Server already started");
        }
    }

    @FXML
    public void onStop() {
        //Se il server è spento lo segnalo
        if(this.conn != null && this.conn.isStarted()) {
            this.logger.info("Start stopping...");
            this.conn.stopServer();
        } else
            this.logger.info("Server already stopped");
    }

    @FXML
    public void onCleanLog() {
          logTxt.clear();
    }

    public LoggerManager getLogger(){ return this.logger;}

}
