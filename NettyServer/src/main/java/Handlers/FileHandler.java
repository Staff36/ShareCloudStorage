package Handlers;

import MessageTypes.FileData;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
@Data
@Log4j
public class FileHandler {
    private File parentDir = Paths.get("ServersStorage", "Unnamed").toFile();
    private File currentDir = parentDir;
    private String sessionCode;

   public void initializeUser(String rootDir, String sessionCode){
       this.parentDir = Paths.get("ServersStorage", rootDir).toFile();
       if (!parentDir.exists()){
           parentDir.mkdir();
       }
       currentDir = parentDir;
       this.sessionCode = sessionCode;
   }

    public void moveToDirectory(String name) {
        File currentFile = Arrays.stream(Objects.requireNonNull(currentDir.listFiles())).filter(x -> x.getName().equals(name)).findFirst().get();
        if (currentFile.isDirectory()) {
            currentDir = currentFile;
        }
    }

    public void moveToParentDirectory(){

        if (!currentDir.equals(parentDir)){
            currentDir = currentDir.getParentFile();
        }
    }

    public void downloadFile(FileData fileData){
        File file = Paths.get(currentDir.toString(), fileData.getName()).toFile();
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
         ras.write(fileData.getData());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            updateListsOfDirectories();
        }
    }
    public FileData prepareFileToUploading(String name){
        File file = Paths.get(currentDir.toString(), name).toFile();
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
           byte[] bytes = new byte[(int) file.length()];
            ras.read(bytes);
            log.info("Sending file");
            return new FileData(sessionCode,name,bytes, 0,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File[] getListFiles(){
        return currentDir.listFiles();
    }

    public void makeDir(String name){
        File file = Paths.get(currentDir.toString(), name).toFile();
        file.mkdir();
    }

    public void updateListsOfDirectories(){
        currentDir = new File(Paths.get(currentDir.getPath()).toString());
        parentDir = new File(Paths.get(currentDir.getPath()).toString());
    }

}
