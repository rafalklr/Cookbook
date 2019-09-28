import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // FXMLLoader - pobranie zasobu .fxml z ścieżki i zapisanie w obiekcie resource
        Parent resource = FXMLLoader.load(getClass().getResource("view/loginView.fxml"));
        // Ustawiamy tytuł okna
        primaryStage.setTitle("Panel logowania");
        Scene scene = new Scene(resource);
        // dodanie stylów do scene
        scene.getStylesheets().add("css/init.css");
        // Ustawiamy zasób fxml w stage-u
        primaryStage.setScene(scene);

        // Powoduje zatrzymianie okna na ekranie
        primaryStage.show();
    }

    // uruchomienie aplikacji
    public static void main(String[] args) {
        launch(args);
    }
}