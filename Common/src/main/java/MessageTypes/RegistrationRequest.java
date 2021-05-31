package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RegistrationRequest implements Serializable {
    String login;
    String eMail;
    String password;
}
