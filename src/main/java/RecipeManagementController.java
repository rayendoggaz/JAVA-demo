import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecipeManagementController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextArea ingredientsTextArea;

    @FXML
    private TextArea instructionsTextArea;

    @FXML
    private TextField cookingTimeTextField;

    @FXML
    private TextField categoryTextField;

    private User loggedInUser;

    public void initialize(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @FXML
    private void addRecipe() {
        try {
            Connection conn = Connexion.getConnection();

            String sql = "INSERT INTO recipes (name, ingredients, instructions, cooking_time, category, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, nameTextField.getText());
            statement.setString(2, ingredientsTextArea.getText());
            statement.setString(3, instructionsTextArea.getText());

            String cookingTimeText = cookingTimeTextField.getText();
            int cookingTime;
            try {
                cookingTime = Integer.parseInt(cookingTimeText);
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid Input", "Cooking time must be a valid integer.");
                return;
            }
            statement.setInt(4, cookingTime);

            statement.setString(5, categoryTextField.getText());

            // Set the user ID
            statement.setInt(6, loggedInUser.getId());

            statement.executeUpdate();

            showSuccessAlert("Recipe Added", "The recipe was added successfully!");

            clearFields();

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameTextField.clear();
        ingredientsTextArea.clear();
        instructionsTextArea.clear();
        cookingTimeTextField.clear();
        categoryTextField.clear();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        openRecipeBrowsingAfterAlert();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openRecipeBrowsingAfterAlert() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_browsing.fxml"));
            Parent root = loader.load();

            RecipeBrowsingController controller = loader.getController();
            controller.initialize(loggedInUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Recipe Browsing");
            stage.show();

            Stage currentStage = (Stage) nameTextField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        openRecipeBrowsing();
    }

    private void openRecipeBrowsing() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_browsing.fxml"));
            Parent root = loader.load();

            RecipeBrowsingController controller = loader.getController();
            controller.initialize(loggedInUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Recipe Browsing");
            stage.show();

            Stage currentStage = (Stage) nameTextField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
