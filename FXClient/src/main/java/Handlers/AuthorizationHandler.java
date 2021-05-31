package Handlers;

import MessageTypes.AuthorizationAnswer;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class AuthorizationHandler {
    @Getter
    private static String sessionCode = "";

    private AuthorizationHandler(){

    }
    public static void checkAnswer(AuthorizationAnswer answer) {
        if (answer.getStatus().equals("Success")){
            sessionCode = answer.getSessionCode();
            log.info("Session number is " + sessionCode);
        }
    }
}
