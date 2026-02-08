import controller.LogInController;
import controller.ResponseController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.shared.ServerResponse;
import model.shared.TypeOfRequest;


public class MailClient extends Application implements ResponseController{

    private LogInController logInController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Controllo se è loggato
        /*Parent root;
        User user = GenericUtility.isLogged();
        if(user != null) {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/MainClient.fxml"));
            root = mainLoader.load();
            MainController mainController = mainLoader.getController();
            mainController.initController(user);
        } else
            root = FXMLLoader.load(getClass().getResource("/LogIn.fxml"));*/
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("/LogIn.fxml"));
        Parent root = logInLoader.load();
        this.logInController = logInLoader.getController();
        //Parent root = FXMLLoader.load(getClass().getResource("/LogIn.fxml"));
        primaryStage.setTitle("Not A Domain - v 1.0");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    //stoppiamo l'executor service
    @Override
    public void stop() throws Exception {
        if(this.logInController != null && this.logInController.getScheduledExec() != null)
            this.logInController.getScheduledExec().shutdownNow();
        exec.shutdownNow();
        super.stop();
    }

    @Override
    public void onSuccess(TypeOfRequest action, ServerResponse response) {}

    @Override
    public void onError(TypeOfRequest action, ServerResponse response) {}

    @Override
    public void onSocketError(Exception e, String errorMessage) {}
}
