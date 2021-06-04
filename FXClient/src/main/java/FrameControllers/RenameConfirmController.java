package FrameControllers;

import Enums.Sides;
import Handlers.AuthorizationHandler;
import Handlers.NetworkHandler;
import MessageTypes.FileImpl;
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
import java.util.ResourceBundle;

@Log4j
@Data
public class RenameConfirmController implements Initializable {
    public TextField filesType;
    private FileImpl file;
    private String labelText;
    private Sides side;
    public Button cancelButton;
    public TextField field;
    public Label commandText;
    private NetworkHandler handler;
    private MainFrameController mfc;

    public RenameConfirmController(FileImpl file, Sides side, MainFrameController mfc) {
        this.file = file;
        this.side = side;
        this.mfc = mfc;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = NetworkHandler.getInstance();
        labelText = file.isFile() ? "Rename file to..." : "Rename directory to ...";
        commandText.setText(labelText);
        if (!file.isFile()){
            filesType.setVisible(false);
        }else{
            String[] typeOfFile = file.getFileName().split("\\.");
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
        File newFile = filesType.getText().isEmpty() ?
                Paths.get(mfc.getFileHandler().getFileByName(file.getFileName()).getAbsolutePath(), field.getText()).toFile() :
                Paths.get(mfc.getFileHandler().getFileByName(file.getFileName()).getParentFile().getAbsolutePath(),field.getText() + "." + filesType.getText()).toFile();
        log.debug(newFile.getAbsolutePath());
        if (side.equals(Sides.CLIENTS_SIDE)){
            mfc.getFileHandler().getFileByName(file.getFileName()).renameTo(newFile);
            mfc.getFileHandler().updateDirectory();
            mfc.repaintClientsSide(mfc.getFileHandler().getCurrentFiles());
        }
        if (side.equals(Sides.SERVERS_SIDE)){
            handler.writeToChannel(new RenameFileRequest(AuthorizationHandler.getSessionCode(), file,
                    new FileImpl(newFile.getName(), newFile.list(), newFile.isFile(),file.isShared(), file.isVirtualFile())));
            mfc.getRenewServersFilesList("");
        }
        closeThisStage();
    }
    private void closeThisStage(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


}
