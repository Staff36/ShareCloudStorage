package Handlers;

import MessageTypes.AuthorizationAnswer;
import lombok.Getter;


public class AuthorizationHandler {
    @Getter
    private static String sessionCode = "";

    private AuthorizationHandler(){

    }
    public static void checkAnswer(AuthorizationAnswer answer) {
        if (answer.getStatus().equals("Success")){
            sessionCode = answer.getClientsNumber();
        }
    }
}
