package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ShareFileRequest implements Serializable {
    FileImpl file;
    String destinator;
}
