package FrameControllers;

import Enums.Sides;
import Handlers.AuthorizationHandler;
import Handlers.NetworkHandler;
import MessageTypes.FileImpl;
import MessageTypes.ListFilesRequest;
import MessageTypes.MakeDirRequest;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

public class MkDirConfirmController {
    public TextField field;
    public Label label;
    Sides side;
    MainFrameController mfc;

    public MkDirConfirmController(Sides side, MainFrameController mfc) {
        this.side = side;
        this.mfc = mfc;
    }

    public void cancel(ActionEvent actionEvent) {
        closeThisStage();
    }

    public void confirm(ActionEvent actionEvent) {
        if (field.getText().contains("\\") || field.getText().contains("/")
                || field.getText().contains("*")
                || field.getText().contains("?")
                || field.getText().contains("<")
                || field.getText().contains("<")
                || field.getText().contains("\"")){
                label.setText("Incorrect symbol in Directory name");
            return;
        }

        if (side.equals(Sides.SERVERS_SIDE)){
            if(Arrays.stream(mfc.getServersFiles()).anyMatch(x->x.getFileName().equals(field.getText()))){
                label.setText("Directory " + field.getText() + "has already exist");
                return;
            }
            NetworkHandler.getInstance().writeToChannel(
                    new MakeDirRequest(AuthorizationHandler.getSessionCode(),field.getText(), new Date().getTime()));
            NetworkHandler.getInstance().writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode(), ""));
        } else {
            File file1 = Paths.get(mfc.getFileHandler().getCurrentDir().getPath(), field.getText()).toFile();
            if (Arrays.stream(mfc.getFileHandler().getCurrentDir().listFiles()).anyMatch(x-> x.equals(file1))){
                label.setText("Directory " + field.getText() + "has already exist");
                return;
            } else {
                file1.mkdir();
            }
        }
        closeThisStage();
    }

    private void closeThisStage(){
        Stage stage = (Stage) field.getScene().getWindow();
        stage.close();
    }
}
