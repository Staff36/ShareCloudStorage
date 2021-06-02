package Handlers;

import MessageTypes.DeleteFileRequest;
import MessageTypes.FileData;
import MessageTypes.FileImpl;
import MessageTypes.RenameFileRequest;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import java.io.File;
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
            log.info("Moving to: " + currentFile.getAbsolutePath());
            currentDir = currentFile;
        }
    }

    public void moveToParentDirectory(){
       log.info("Parent dir is: " + parentDir.getAbsolutePath());
       log.info(!currentDir.equals(parentDir));
        if (!currentDir.equals(parentDir)){
            currentDir = currentDir.getParentFile();
            log.info("Moving to Parent: " + currentDir.getAbsolutePath());
            log.info("Current dir is: " + currentDir.getAbsolutePath());
        }
    }

    public void downloadFile(FileData fileData){
        File file = Paths.get(currentDir.toString(), fileData.getName()).toFile();
        log.info("Downloading file: " +file.getName());
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
            log.info("Sending file: " + file.getName());
            return new FileData(sessionCode,name,bytes, 0,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FileImpl[] getListFiles(){

       return Arrays.stream(currentDir.listFiles()).map(x->new FileImpl(x.getName(), x.list(), x.isFile())).toArray(FileImpl[]::new);
    }

    public void makeDir(String name){
        File file = Paths.get(currentDir.getPath(), name).toFile();
        log.info("Making dir: " + file.getAbsolutePath());
        file.mkdir();
    }

    public void updateListsOfDirectories(){
        currentDir = new File(Paths.get(currentDir.getPath()).toString());
        parentDir = new File(Paths.get(parentDir.getPath()).toString());
    }

    public void renameFile(RenameFileRequest renameFileRequest){
        String name = renameFileRequest.getOldFile().getFileName();
        File currentFile = Arrays.stream(Objects.requireNonNull(currentDir.listFiles())).filter(x -> x.getName().equals(name)).findFirst().get();
        File newFile;
        newFile = Paths.get(currentFile.getParentFile().getPath(), renameFileRequest.getNewFile().getFileName()).toFile();
        currentFile.renameTo(newFile);
    }

    public void deleteFile(DeleteFileRequest dfr) {
        if (dfr.getFile() == null || dfr.getFile().equals(parentDir)) {
        return;
        }
        String name = dfr.getFile().getFileName();
        File currentFile = Arrays.stream(Objects.requireNonNull(currentDir.listFiles())).filter(x -> x.getName().equals(name)).findFirst().get();
        currentFile.delete();

    }
}
