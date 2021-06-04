package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileImpl implements Serializable {
    private String fileName;
    private String[] listOfChild;
    private boolean isFile;
    private boolean isShared;
    private boolean isVirtualFile;


}
