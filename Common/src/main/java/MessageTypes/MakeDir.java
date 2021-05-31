package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class MakeDir  implements Serializable {
    String sessionCode;
    String name;
}
