package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileImpl implements Serializable {
    String fileName;
    String[] listOfChild;
    boolean isFile;
}
