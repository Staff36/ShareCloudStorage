package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;


@Data
@AllArgsConstructor
public class SyncFolderRequest implements Serializable {
    private FileImpl dir;
}
