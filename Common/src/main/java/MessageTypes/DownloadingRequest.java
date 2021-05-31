package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DownloadingRequest implements Serializable {
    String codeSession;
    String filename;

}
