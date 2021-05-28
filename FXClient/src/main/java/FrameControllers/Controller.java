package FrameControllers;

import Handlers.NetworkHandler;
import MessageTypes.AuthorizationRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Controller implements Initializable {

    public Button login;
    public PasswordField passwordField;
    public TextField loginField;
    public Label authLabel;
    private NetworkHandler handler;
    private Consumer<Object> callback;


    public void login(ActionEvent actionEvent) {
        AuthorizationRequest request = new AuthorizationRequest(loginField.getText(), passwordField.getText());
        handler.setMainCallBack(callback);
        handler.writeToChannel(request);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        callback = this::readAuthorizationStatus;
        login.setDisable(true);
        handler = NetworkHandler.getInstance();
    }

    public void updateButton(KeyEvent keyEvent) {
        if (loginField.getText().isEmpty()){
            login.setDisable(true);
        } else {
            login.setDisable(false);
        }
    }

    private void readAuthorizationStatus(Object o){
        String status = (String) o;
        if (status.equals("Success")){
            closeThisFrame();
            openMainFrame();
        } else {
            authLabel.setText(status);
        }
    }

    private void openMainFrame(){
        try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Frames/mainFrame.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Share Cloud Storage");
        stage.setScene(scene);
        stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void closeThisFrame(){
        Stage stage = (Stage) login.getScene().getWindow();
        stage.close();
    }
}
