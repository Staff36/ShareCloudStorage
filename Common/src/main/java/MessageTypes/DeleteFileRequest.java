package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class DeleteFileRequest implements Serializable {
    File file;
    String sessionCode;
}
