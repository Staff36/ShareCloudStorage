package Entities;

import lombok.Data;
import java.io.Serializable;

@Data
public class Confirmation implements Serializable {
    String email;
    int code;
}
