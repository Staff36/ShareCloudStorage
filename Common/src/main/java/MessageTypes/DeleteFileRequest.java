package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class DeleteFileRequest implements Serializable {
    private FileImpl file;
    private String sessionCode;
}
