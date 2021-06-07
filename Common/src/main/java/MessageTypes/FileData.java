package MessageTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileData implements Serializable {
    private String sessionCode;
    private String name;
    private byte [] data;
    private int part;
    private int totalPartsValue;
    private long lastModified;
    private int partsSize;
}
