import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage browsingStage = new Stage();
        Parent LoginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
        browsingStage.setScene(new Scene(LoginRoot));
        browsingStage.setTitle("Create An Account");
        browsingStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}