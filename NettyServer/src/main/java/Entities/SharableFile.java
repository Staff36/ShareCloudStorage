package Entities;

import lombok.Data;

import java.util.Map;
@Data
public class SharableFile {
    String owner;
    Map<String,String> pathForRecipient;
    String realPath;

}
