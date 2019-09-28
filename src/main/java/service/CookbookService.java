package service;

import controller.CookbookController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Ingredient;
import model.Recipe;
import model.enums.Level;
import model.enums.Meal;
import model.enums.Type;
import utility.InMemoryDB;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class CookbookService {
    public void setSelectedRecipe(
            Recipe recipe, TextField tf_title, TextArea ta_description, TextField tf_type,
            TextField tf_level, TextField tf_meal, TextField tf_time, ListView tf_ingredients,
            ImageView iv_image) {
        // przypisanie pól obiektu Recipe do kontrolek
        tf_title.setText(recipe.getTitle());
        ta_description.setText(recipe.getDescription());
        tf_meal.setText(recipe.getMeal().name());
        tf_level.setText(recipe.getLevel().name());
        tf_type.setText(recipe.getType().name());
        tf_time.setText(String.valueOf(recipe.getPrepareTime()));
        // przypisanie listy składników do LV
        ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        ingredients.addAll(recipe.getIngredients());
        tf_ingredients.setItems(ingredients);
        // ustawienie obrazka na IV
        iv_image.setImage(new Image(recipe.getImagePath()));
    }

    public void setTableProperty(
            TableColumn c_title, TableColumn c_description, TableColumn c_time, TableColumn c_meal,
            TableColumn c_level, TableColumn c_type, TableColumn c_ingredients) {
        c_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        c_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        c_time.setCellValueFactory(new PropertyValueFactory<>("prepareTime"));
        c_meal.setCellValueFactory(new PropertyValueFactory<>("meal"));
        c_level.setCellValueFactory(new PropertyValueFactory<>("level"));
        c_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        c_ingredients.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
    }

    public void setTableItems(TableView tbl_recipes, ObservableList recipes_fx) {
        tbl_recipes.setItems(recipes_fx);
    }

    // metoda pobierająca obiekt z wybranego rekordu
    public Recipe getRecipeFromSelectedRow(TableView<Recipe> tbl_recipe) {
        return tbl_recipe.getSelectionModel().getSelectedItem();
    }

    // metoda usuwająca recepture po obiekcie Recipe
    public void deleteRecipe(Recipe recipe, ObservableList recipes_fx) {
        if (recipe != null) {
            recipes_fx.remove(recipe);
        }
    }
    // okienko dialogowa do upload-u plików
    public String uploadFile(ImageView e_view){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zdjęcie");
        fileChooser.setInitialDirectory(new File("C:\\Users\\PROXIMO\\Desktop\\Cookbook\\Cookbook\\src\\main\\resources\\img"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("obrazy", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null) {
            String imagepath = file.getPath();
            String direct_path = "C:\\Users\\PROXIMO\\Desktop\\Cookbook\\Cookbook\\src\\main\\resources";
            imagepath = imagepath.replace(direct_path, "");
            System.out.println("file:"+imagepath);
            // wyświetlenie obrazka do podglądu
            e_view.setImage(new Image(imagepath));
            // zapis ścieżki do obiektu Recipe
            return imagepath;
        }
        return null;
    }
    // wprowadzenie danych do combo
    public void setMealItems(ComboBox e_meal){
        ObservableList<Meal> meals = FXCollections.observableArrayList();
        meals.addAll(Meal.values());
        e_meal.setItems(meals);
    }
    public void setLevelItems(ComboBox e_level){
        ObservableList<Level> levels = FXCollections.observableArrayList();
        levels.addAll(Level.values());
        e_level.setItems(levels);
    }
    public void setTypeItems(ComboBox e_type){
        ObservableList<Type> types = FXCollections.observableArrayList();
        types.addAll(Type.values());
        e_type.setItems(types);
    }
    public boolean validRecipe(TextField e_title, TextArea e_description, Spinner<Integer> e_time,
                               ComboBox e_meal, ComboBox e_level, ComboBox e_type){
        if(e_title.getText().equals("") || e_description.getText().equals("") ||
                e_meal.getValue() == null || e_level.getValue() == null || e_type.getValue() == null){
            new WindowService().getAlert(
                    Alert.AlertType.ERROR,
                    "Błąd dodawania receptury",
                    "Błąd dodawania receptury",
                    "Należy uzupełnić wszystkie wymagane pola");
            return false;
        }
        return true;
    }
    public void saveRecipe(TextField e_title, TextArea e_description, Spinner<Integer> e_time,
                           ComboBox e_meal, ComboBox e_level, ComboBox e_type, String imagePath, TableView tbl_recipes,
                           ListView lv_ingredientsView, ComboBox e_ingredients, ImageView e_view){
        if(validRecipe(e_title, e_description, e_time, e_meal, e_level, e_type)){
            // utworzenie nowej receptury
            Recipe recipe = new Recipe(e_title.getText(), e_description.getText(), e_time.getValue(), imagePath,
                    (Meal) e_meal.getValue(), (Level) e_level.getValue(), (Type) e_type.getValue(),
                    lv_ingredientsView.getItems());
            if(imagePath == null){
                recipe.setImagePath("img/intro.png");
            } else {
                recipe.setImagePath(imagePath);
            }
            // zapisanie receptury do tablicy receptur
            InMemoryDB.recipes.add(recipe);
            CookbookController.recipes_fx.clear();
            CookbookController.recipes_fx.addAll(InMemoryDB.recipes);
            setTableItems(tbl_recipes, CookbookController.recipes_fx);
            // czyszczenie pól
            e_title.clear();
            e_description.clear();
            e_level.setValue(null);
            e_type.setValue(null);
            e_meal.setValue(null);
            e_ingredients.setValue(null);
            lv_ingredientsView.setItems(null);
            e_time.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 180, 1, 5));
            e_view.setImage(null);
            //
        }
    }
    public void setIngredientsCombo(ComboBox e_ingredients, ArrayList<Ingredient> ingredients){
        CookbookController.ingredients_fx.addAll(ingredients);
        e_ingredients.setItems(CookbookController.ingredients_fx);
    }
    public void updateIngredientsCombo(ComboBox e_ingredients, ListView lv_ingredientsView, Ingredient ingredient){
        CookbookController.ingredients_fx.removeAll(lv_ingredientsView.getItems());
        System.out.println(CookbookController.ingredients_fx);
        e_ingredients.setItems(CookbookController.ingredients_fx);
    }

}