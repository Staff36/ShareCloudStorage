package Handlers;

import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
@Log4j
@Data
public class FileHandler {

    private File currentDir = File.listRoots()[0];
    private File[] currentFiles;

    public void moveOrOpenFile(String filename){
        File selectedFile = Arrays.stream(currentDir.listFiles()).filter(x -> x.getAbsolutePath().equals(filename) || x.getName().equals(filename)).findFirst().orElse(currentDir);
        try {
            if (selectedFile.isFile()){
                Desktop.getDesktop().open(currentDir);
            } else {
                currentDir = selectedFile;
                currentFiles = selectedFile.listFiles();
            }
        } catch (IOException e) {
                log.error("Exception of openinig File ", e);
        }
    }

    public boolean isRoot(String absolutePath){
        return Arrays.stream(File.listRoots()).anyMatch(x->x.getAbsolutePath().equals(absolutePath));
    }

    public String[] getListOfFileNames(){
        return Arrays.stream(currentFiles).map(x -> isRoot(x.getAbsolutePath()) ? x.getAbsolutePath() : x.getName()).toArray(String[]::new);
    }

    public String[] getListOfFileNames(File[] files){
        return Arrays.stream(files).map(x -> isRoot(x.getAbsolutePath()) ? x.getAbsolutePath() : x.getName()).toArray(String[]::new);
    }

    public File getFileByName(String name){
      return Arrays.stream(currentFiles).filter(x -> x.getAbsolutePath().equals(name) || x.getName().equals(name)).findFirst().orElse(null);
    }

    public String[] moveUp(){
        if (isRoot(currentDir.getAbsolutePath())){
            currentFiles = File.listRoots();
            return Arrays.stream(currentFiles).map(x-> x.getAbsolutePath()).toArray(String[]::new);
        } else {
            currentDir = currentDir.getParentFile();
            currentFiles = currentDir.listFiles();
            return Arrays.stream(currentFiles).map(x-> x.getName()).toArray(String[]::new);
        }
    }





}
