package FrameControllers;

import Handlers.NetworkHandler;
import MessageTypes.FileImpl;
import MessageTypes.ShareFileRequest;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ShareConfirmController {
    private FileImpl file;
    public TextField textField;

    public ShareConfirmController(FileImpl file) {
        this.file = file;
    }


    public void cancel(ActionEvent actionEvent) {
        closeThisStage();
    }

    public void confirm(ActionEvent actionEvent) {
        if (file ==null || !file.isFile()){
            return;
        }
        NetworkHandler networkHandler = NetworkHandler.getInstance();
        networkHandler.writeToChannel(new ShareFileRequest(file, textField.getText()));
        closeThisStage();
    }

    private void closeThisStage(){
        Stage stage = (Stage) textField.getScene().getWindow();
        stage.close();
    }
}
