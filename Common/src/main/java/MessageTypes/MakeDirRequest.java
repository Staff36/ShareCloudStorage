package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class MakeDirRequest implements Serializable {
    String sessionCode;
    String name;
}
