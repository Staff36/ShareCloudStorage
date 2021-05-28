package Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
public class User {
    private String user;
    private String password;
    private int code;
    private File rootDir;

    public User(String user, String password, File file) {
        this.rootDir = file;
        this.user = user;
        this.password = password;
        this.code = user.hashCode() + password.hashCode();
    }


    public void calculateSessionCode(){
        int userHash = 0;
        int passwordHAsh = 0;
        if (user != null){
            userHash = user.hashCode();
        }
        if (password != null){
            passwordHAsh = password.hashCode();
        }
        code = userHash + passwordHAsh;
    }
}
