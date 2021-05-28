package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileData implements Serializable {
    private String sessionCode;
    private String name;
    private byte [] data;
    // Delimit file on a parts If file has a big size
    private int part;
    private int totalPartsValue;
}
