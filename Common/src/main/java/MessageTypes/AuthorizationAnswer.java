package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AuthorizationAnswer implements Serializable {
    private String clientsNumber;
    private String status;
}
