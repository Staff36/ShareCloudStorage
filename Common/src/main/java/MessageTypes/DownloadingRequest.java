package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadingRequest {
    String codeSession;
    String filename;

}
