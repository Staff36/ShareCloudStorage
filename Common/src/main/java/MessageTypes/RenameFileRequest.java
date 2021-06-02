package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class RenameFileRequest implements Serializable {

    private String sessionCode;
    private FileImpl oldFile;
    private FileImpl newFile;

}
