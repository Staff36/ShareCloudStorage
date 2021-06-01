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
import lombok.NoArgsConstructor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmDeleteFileController implements Initializable {

    public Label label;
    private File file;
    private Sides side;
    private String labelText;
    private NetworkHandler networkHandler;
    private MainFrameController mfc;

    public ConfirmDeleteFileController(File file, Sides side, MainFrameController mfc) {
        this.file = file;
        this.side = side;
        labelText = file.isFile() ? "You really want to delete the file " + file.getName() : "You really want to delete the directory " + file.getName();
        this.mfc = mfc;
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
            mfc.getFileHandler().updateDirectory();
            mfc.repaintClientsSide(mfc.getFileHandler().getCurrentFiles());
        } else {
            networkHandler.writeToChannel(new DeleteFileRequest(file, AuthorizationHandler.getSessionCode()));
            mfc.getRenewServersFilesList("");
        }
        closeThisStage();
    }

    private void closeThisStage(){
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
}
