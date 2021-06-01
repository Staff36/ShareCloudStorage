package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MovingToDirRequest implements Serializable {
    String sessionCode;
    String dirName;
}
