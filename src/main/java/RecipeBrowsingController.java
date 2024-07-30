import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecipeBrowsingController {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<Recipe> recipeListView;

    private User loggedInUser;

    public void initialize(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        System.out.println("Logged-in User ID: " + loggedInUser.getId());

        populateCategoryComboBox();
        loadUserRecipes();

        recipeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    showRecipeDetails(newValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void deleteSelectedRecipe(ActionEvent event) {
        int selectedIndex = recipeListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Recipe selectedRecipe = recipeListView.getItems().get(selectedIndex);
            deleteRecipeFromDatabase(selectedRecipe.getName());
            recipeListView.getItems().remove(selectedIndex);
        }
    }

    private void deleteRecipeFromDatabase(String recipeName) {
        try (Connection connection = Connexion.getConnection()) {
            String query = "DELETE FROM recipes WHERE name = ? AND user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, recipeName);
                statement.setInt(2, loggedInUser.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRecipeManagement(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_management.fxml"));
        Parent root = loader.load();

        RecipeManagementController controller = loader.getController();
        controller.initialize(loggedInUser);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Recipe Management");
        stage.show();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void openUpdateRecipe(ActionEvent event) throws IOException {
        Recipe selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update_recipe.fxml"));
            Parent root = loader.load();

            UpdateRecipeController controller = loader.getController();
            controller.initializeFields(selectedRecipe, loggedInUser);
            controller.setSelectedRecipe(selectedRecipe.getName());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Recipe");
            stage.show();
        }
    }

    private void populateCategoryComboBox() {
        try {
            Connection conn = Connexion.getConnection();
            String sql = "SELECT DISTINCT category FROM recipes WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, loggedInUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                categoryComboBox.getItems().add(category);
            }

            statement.close();
            conn.close();

            categoryComboBox.setOnAction(event -> {
                String selectedCategory = categoryComboBox.getValue();
                if (selectedCategory != null) {
                    loadRecipesByCategory(selectedCategory);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecipesByCategory(String category) {
        try {
            Connection conn = Connexion.getConnection();
            String sql = "SELECT * FROM recipes WHERE category = ? AND user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, category);
            statement.setInt(2, loggedInUser.getId());
            ResultSet resultSet = statement.executeQuery();

            recipeListView.getItems().clear();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String categoryValue = resultSet.getString("category");
                String ingredients = resultSet.getString("ingredients");
                String instructions = resultSet.getString("instructions");
                int cookingTime = resultSet.getInt("cooking_time");

                Recipe recipe = new Recipe(name, categoryValue, ingredients.split(","), instructions, cookingTime);
                recipeListView.getItems().add(recipe);
            }

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUserRecipes() {
        recipeListView.getItems().clear();

        try {
            Connection conn = Connexion.getConnection();
            String sql = "SELECT * FROM recipes WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, loggedInUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String ingredients = resultSet.getString("ingredients");
                String instructions = resultSet.getString("instructions");
                int cookingTime = resultSet.getInt("cooking_time");

                Recipe recipe = new Recipe(name, category, ingredients.split(","), instructions, cookingTime);
                recipeListView.getItems().add(recipe);
            }

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchRecipes() {
        recipeListView.getItems().clear();

        String searchTerm = searchTextField.getText();

        try {
            Connection conn = Connexion.getConnection();
            String sql = "SELECT * FROM recipes WHERE (name LIKE ? OR ingredients LIKE ? OR category LIKE ?) AND user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            statement.setString(3, "%" + searchTerm + "%");
            statement.setInt(4, loggedInUser.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String ingredients = resultSet.getString("ingredients");
                String instructions = resultSet.getString("instructions");
                int cookingTime = resultSet.getInt("cooking_time");

                Recipe recipe = new Recipe(name, category, ingredients.split(","), instructions, cookingTime);
                recipeListView.getItems().add(recipe);
            }

            statement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showRecipeDetails(Recipe selectedRecipe) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_details.fxml"));
        Parent root = loader.load();

        RecipeDetailsController controller = loader.getController();
        controller.initialize(loggedInUser, selectedRecipe);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Recipe Details");
        stage.show();

        Stage currentStage = (Stage) recipeListView.getScene().getWindow();
        currentStage.close();
    }

}
