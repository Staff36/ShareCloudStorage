import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
public class User {
    public User(String user, String password) {
        this.user = user;
        this.password = password;
        this.code = user.hashCode() + password.hashCode();
    }

    String user;
   String password;
   int code;
}
