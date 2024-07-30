import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateRecipeController {

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

    private String selectedRecipe;

    private User loggedInUser;

    public void setSelectedRecipe(String recipeName) {
        if (recipeName == null) {
            System.out.println("setSelectedRecipe called with null.");
        } else {
            System.out.println("setSelectedRecipe called with: " + recipeName);
        }
        this.selectedRecipe = recipeName;
    }

    @FXML
    private void saveRecipe(ActionEvent event) throws IOException {
        if (selectedRecipe == null) {
            System.out.println("No recipe selected for update.");
            return;
        }
        try {
            Connection conn = Connexion.getConnection();

            String sql = "UPDATE recipes SET name = ?, ingredients = ?, instructions = ?, cooking_time = ?, category = ? WHERE name = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nameTextField.getText());
            statement.setString(2, ingredientsTextArea.getText());
            statement.setString(3, instructionsTextArea.getText());
            statement.setInt(4, Integer.parseInt(cookingTimeTextField.getText()));
            statement.setString(5, categoryTextField.getText());
            statement.setString(6, selectedRecipe);

            System.out.println("Executing SQL query: " + statement.toString());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Recipe updated successfully!");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_browsing.fxml"));
                Parent root = loader.load();

                RecipeBrowsingController controller = loader.getController();
                controller.initialize(loggedInUser);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Recipe Browsing");
                stage.show();

                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
            } else {
                System.out.println("Failed to update recipe. Recipe name: " + selectedRecipe);
            }

            statement.close();
            conn.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
        }
    }

    public void initializeFields(Recipe selectedRecipe, User loggedInUser) {
        this.loggedInUser = loggedInUser;
        nameTextField.setText(selectedRecipe.getName());
        ingredientsTextArea.setText(String.join(", ", selectedRecipe.getIngredients()));
        instructionsTextArea.setText(selectedRecipe.getInstructions());
        cookingTimeTextField.setText(String.valueOf(selectedRecipe.getCookingTime()));
        categoryTextField.setText(selectedRecipe.getCategory());
    }
}
