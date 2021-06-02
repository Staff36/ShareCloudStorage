package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FilesList implements Serializable {
    private FileImpl[] files;
}
