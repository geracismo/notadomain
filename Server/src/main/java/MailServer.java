import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MailServer extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/MainServer.fxml"));
        Parent root = mainLoader.load();
        this.mainController = mainLoader.getController();

        primaryStage.setTitle("MailServer - v 1.0");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        this.mainController.onStop();
        this.mainController.getLogger().saveLogFile();
        super.stop();
    }

    public static void main(String[] args) { launch(args); }
}
