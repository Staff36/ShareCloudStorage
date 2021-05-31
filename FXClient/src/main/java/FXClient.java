import FrameControllers.MainFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FrameControllers/authFrame.fxml"));
        primaryStage.setTitle("Share Cloud Storage: Authorization");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")))
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
