package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AuthorizationAnswer implements Serializable {
    private String sessionCode;
    private String status;
}
