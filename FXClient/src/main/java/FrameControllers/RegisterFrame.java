package FrameControllers;

import Handlers.NetworkHandler;
import MessageTypes.RegistrationRequest;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class RegisterFrame implements Initializable {
    public Label timer;
    public Label repeatEmailText;
    public PasswordField password;
    public PasswordField confirmPassword;
    public TextField eMail;
    public TextField login;
    public Label confirmText;
    public Label textForSending;
    public TextField confirmField;
    public Button confirmCodeButton;
    private NetworkHandler networkHandler = NetworkHandler.getInstance();
    private Consumer<Object> consumer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        consumer = this::handleIncomingMessage;
        networkHandler.setMainCallBack(consumer);
        timer.setVisible(false);
        repeatEmailText.setVisible(false);
        confirmText.setVisible(false);
        textForSending.setVisible(false);
        confirmField.setVisible(false);
        confirmCodeButton.setVisible(false);
    }

    public void closeThis(ActionEvent actionEvent) {
        FrameSwitcher.openFrame("authFrame.fxml", "Share Cloud Storage: Authorization");
        closeThisFrame();
    }

    private void closeThisFrame(){
        Stage stage = (Stage) login.getScene().getWindow();
        stage.close();
    }

    public void sendRegisterRequest(ActionEvent actionEvent) {
        if (login.getText().isEmpty()){

            return;
        }
        if (eMail.getText().isEmpty()){
            return;
        }
        if (password.getText().isEmpty()){
            return;
        }
        if (!confirmPassword.getText().equals(password.getText())){
            return;
        }
        networkHandler.writeToChannel(new RegistrationRequest(login.getText(), eMail.getText(), password.getText()));

    }

    public void checkAuthorizationCode(ActionEvent actionEvent) {


    }

    private void handleIncomingMessage(Object o){


    }

    public void confirmCode(ActionEvent actionEvent) {
    }
}
