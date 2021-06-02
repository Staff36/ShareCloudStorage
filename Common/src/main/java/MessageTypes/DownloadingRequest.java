package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DownloadingRequest implements Serializable {
    private String codeSession;
    private String filename;

}
