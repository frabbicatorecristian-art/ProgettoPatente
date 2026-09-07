import java.io.File;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompilerCheck {
    public static void main(String[] args) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("No compiler found");
            return;
        }
        int result = compiler.run(null, null, null, 
            "-d", "C:/Users/raffy/Desktop/ProgettoPatente-master/target/classes",
            "--module-path", "C:/Users/raffy/.m2/repository/org/openjfx/javafx-controls/17.0.12/javafx-controls-17.0.12-win.jar;C:/Users/raffy/.m2/repository/org/openjfx/javafx-graphics/17.0.12/javafx-graphics-17.0.12-win.jar;C:/Users/raffy/.m2/repository/org/openjfx/javafx-base/17.0.12/javafx-base-17.0.12-win.jar;C:/Users/raffy/.m2/repository/org/openjfx/javafx-fxml/17.0.12/javafx-fxml-17.0.12-win.jar",
            "--add-modules", "javafx.controls,javafx.fxml",
            "C:/Users/raffy/Desktop/ProgettoPatente-master/src/main/java/controller/DashboardController.java"
        );
        System.out.println("Result: " + result);
    }
}
