package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MakeDir {
    String sessionCode;
    String name;
}
