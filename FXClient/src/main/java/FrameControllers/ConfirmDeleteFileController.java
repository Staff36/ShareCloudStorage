package FrameControllers;

import Enums.Sides;
import Handlers.AuthorizationHandler;
import Handlers.NetworkHandler;
import MessageTypes.DeleteFileRequest;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmDeleteFileController implements Initializable {

    public Label label;
    private File file;
    private Sides side;
    private String labelText;
    private NetworkHandler networkHandler;
    public ConfirmDeleteFileController(File file, Sides side) {
        this.file = file;
        this.side = side;
        labelText = file.isFile() ? "You really want to delete file " + file.getName() : "You really want to directory" + file.getName();
        networkHandler = NetworkHandler.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(labelText);
    }

    public void cancel(ActionEvent actionEvent) {
        closeThisStage();
    }

    public void confirm(ActionEvent actionEvent) {
        if (side.equals(Sides.CLIENTS_SIDE)){
            file.delete();
        } else {
            networkHandler.writeToChannel(new DeleteFileRequest(file, AuthorizationHandler.getSessionCode()));
        }
        closeThisStage();
    }

    private void closeThisStage(){
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
}
