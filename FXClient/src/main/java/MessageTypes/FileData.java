package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.File;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileData implements Serializable {
    String sessionCode;
    String name;
    File file;
}
