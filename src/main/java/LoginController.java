import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button cancelButton;

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User authenticatedUser = authenticate(username, password);
        if (authenticatedUser != null) {
            showAlert(AlertType.INFORMATION, "Success", "Login successful!");
            closeWindow();
            openMainPage(authenticatedUser);
        } else {
            showAlert(AlertType.ERROR, "Error", "Invalid username or password!");
        }
    }

    private User authenticate(String username, String password) {
        try {
            Connection conn = Connexion.getConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                return new User(userId, username);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void openMainPage(User loggedInUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_browsing.fxml"));
            Parent root = loader.load();

            RecipeBrowsingController controller = loader.getController();
            controller.initialize(loggedInUser);

            Stage stage = new Stage();
            stage.setTitle("Main Page");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSignUpPage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("CreateUserAccount.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login Page");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
