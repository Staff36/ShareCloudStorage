package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ConformationRequest implements Serializable {
    private int code;
    private String email;
}
