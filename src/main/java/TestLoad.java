
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestLoad extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        File dir = new File("src/main/resources/view");
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".fxml")) {
                try {
                    System.out.println("Loading " + f.getName());
                    FXMLLoader loader = new FXMLLoader(f.toURI().toURL());
                    loader.load();
                    System.out.println("SUCCESS: " + f.getName());
                } catch (Exception e) {
                    System.out.println("ERROR IN " + f.getName() + ": " + e.getMessage());
                }
            }
        }
        System.exit(0);
    }
}
