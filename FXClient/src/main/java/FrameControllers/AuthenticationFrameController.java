package FrameControllers;

import Handlers.NetworkHandler;
import MessageTypes.AuthorizationRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AuthenticationFrameController implements Initializable {

    public Button login;
    public PasswordField passwordField;
    public TextField loginField;
    public Label authLabel;
    public Label infoLabel;
    private NetworkHandler handler;
    private Consumer<Object> callback;


    public void login(ActionEvent actionEvent) {
        AuthorizationRequest request = new AuthorizationRequest(loginField.getText(), passwordField.getText());

        handler.writeToChannel(request);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        callback = o -> Platform.runLater(()->readAuthorizationStatus(o));
        login.setDisable(true);
        handler = NetworkHandler.getInstance();
        handler.setMainCallBack(callback);
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
            FrameSwitcher.openFrame("mainFrame.fxml", "Share Cloud Storage");
        } else {
            infoLabel.setVisible(false);
            authLabel.setText(status);
        }
    }



    private void closeThisFrame(){
        Stage stage = (Stage) login.getScene().getWindow();
        stage.close();
    }

    public void registerNewUser(ActionEvent actionEvent) {
        closeThisFrame();
        FrameSwitcher.openFrame("registerFrame.fxml", "Register new user");
    }
}
