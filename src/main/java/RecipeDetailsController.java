import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecipeDetailsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label ingredientsLabel;

    @FXML
    private Label instructionsLabel;

    @FXML
    private Label cookingTimeLabel;

    @FXML
    private Label categoryLabel;

    private Recipe selectedRecipe;

    private User loggedInUser;

    public void initialize(User loggedInUser, Recipe selectedRecipe) {
        this.loggedInUser = loggedInUser;
        setRecipeDetails(selectedRecipe);
    }

    public void setRecipeDetails(Recipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        nameLabel.setText(selectedRecipe.getName());
        ingredientsLabel.setText(String.join(", ", selectedRecipe.getIngredients()));
        instructionsLabel.setText(selectedRecipe.getInstructions());
        cookingTimeLabel.setText(selectedRecipe.getCookingTime() + " minutes");
        categoryLabel.setText(selectedRecipe.getCategory());
    }

    @FXML
    private void exportRecipe(ActionEvent event) {
        if (selectedRecipe != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Recipe");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                saveRecipeToFile(file);
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "No recipe selected to export.");
        }
    }

    private void saveRecipeToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Name: " + selectedRecipe.getName() + "\n");
            writer.write("Category: " + selectedRecipe.getCategory() + "\n");
            writer.write("Ingredients: " + String.join(", ", selectedRecipe.getIngredients()) + "\n");
            writer.write("Instructions: " + selectedRecipe.getInstructions() + "\n");
            writer.write("Cooking Time: " + selectedRecipe.getCookingTime() + " minutes\n");
            showAlert(AlertType.INFORMATION, "Success", "Recipe exported successfully!");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to export recipe.");
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openUpdateRecipe(ActionEvent event) {
        if (selectedRecipe != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("update_recipe.fxml"));
                Parent root = loader.load();

                UpdateRecipeController controller = loader.getController();
                controller.initializeFields(selectedRecipe, loggedInUser);
                controller.setSelectedRecipe(selectedRecipe.getName());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Update Recipe");
                stage.show();

                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
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
    }
}