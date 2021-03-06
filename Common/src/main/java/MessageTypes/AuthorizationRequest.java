package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class AuthorizationRequest implements Serializable {
    private String login;
    private String password;
}
