package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConformationRequest {
    int code;
    String email;
}
