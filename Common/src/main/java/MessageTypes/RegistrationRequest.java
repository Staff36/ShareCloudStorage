package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RegistrationRequest implements Serializable {
    private String login;
    private String eMail;
    private String password;
}
