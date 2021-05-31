package FrameControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class FrameSwitcher {
    public static void openFrame(String frame, String title){
        Parent parent;
        try {
            parent = FXMLLoader.load(FrameSwitcher.class.getResource(frame));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
