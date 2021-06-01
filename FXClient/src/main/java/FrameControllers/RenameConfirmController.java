package FrameControllers;

import Enums.Sides;
import Handlers.AuthorizationHandler;
import Handlers.FileHandler;
import Handlers.NetworkHandler;
import MessageTypes.RenameFileRequest;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;

@Log4j
@Data
public class RenameConfirmController implements Initializable {
    public TextField filesType;
    private File file;
    private String labelText;
    private Sides side;
    public Button cancelButton;
    public TextField field;
    public Label commandText;
    private NetworkHandler handler;
    private MainFrameController mfc;

    public RenameConfirmController(File file, Sides side, MainFrameController mfc) {
        this.file = file;
        this.side = side;
        this.mfc = mfc;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = NetworkHandler.getInstance();
        labelText = file.isFile() ? "Rename file to..." : "Rename directory to ...";
        commandText.setText(labelText);
        if (file.isDirectory()){
            filesType.setVisible(false);
        }else{
            String[] typeOfFile = file.getName().split("\\.");
            filesType.setText(typeOfFile[typeOfFile.length-1]);
        }
    }

    public void cancel(ActionEvent actionEvent) {
        closeThisStage();
    }

    public void confirm(ActionEvent actionEvent) {
        if (field.getText().isEmpty()){
            return;
        }
        log.info("Old File path" + file.getParentFile().getAbsolutePath() + " name " + file.getName());
        File newFile = filesType.getText().isEmpty() ? Paths.get(file.getParentFile().getPath(), field.getText()).toFile() : Paths.get(file.getParentFile().getPath(),field.getText() + "." + filesType.getText()).toFile();
        log.info("File new path: " + newFile.getAbsolutePath());
        if (side.equals(Sides.CLIENTS_SIDE)){
            file.renameTo(newFile);
            mfc.getFileHandler().updateDirectory();
            mfc.repaintClientsSide(mfc.getFileHandler().getCurrentFiles());
        }
        if (side.equals(Sides.SERVERS_SIDE)){
            handler.writeToChannel(new RenameFileRequest(AuthorizationHandler.getSessionCode(), file, newFile));
            mfc.getRenewServersFilesList("");
        }
        closeThisStage();
    }
    private void closeThisStage(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
