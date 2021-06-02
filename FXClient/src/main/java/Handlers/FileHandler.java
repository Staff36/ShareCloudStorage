package Handlers;

import MessageTypes.FileData;
import MessageTypes.FileImpl;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j
@Data
public class FileHandler {
    private List<DirectoryListener> listenedDirectories = new ArrayList<>();
    private File currentDir = File.listRoots()[0];
    private File[] currentFiles;

    public void moveOrOpenFile(String filename){
        File selectedFile = Arrays.stream(currentDir.listFiles()).filter(x -> x.getAbsolutePath().equals(filename) || x.getName().equals(filename)).findFirst().orElse(currentDir);
        log.info("Selected file is: " + selectedFile.getAbsolutePath());
        try {
            if (selectedFile.isFile()){
                log.info("Selected file is FILE!");
                Desktop.getDesktop().open(selectedFile);
            } else {
                log.info("Selected file is Directory");
                currentDir = selectedFile;
                log.info("List of files " + currentDir.list().length);
                if (currentDir.listFiles().length == 0){
                    currentFiles = new File[0];
                } else {
                currentFiles = selectedFile.listFiles();
                }
            }
        } catch (IOException e) {
                log.error("Exception of opening File ", e);
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
    public void downloadFile(FileData fileData){
        File file = Paths.get(currentDir.toString(), fileData.getName()).toFile();
        try (RandomAccessFile ras = new RandomAccessFile(file,"rw")){
            log.info("Writing file: " + fileData.getName() + " into " + file.getAbsolutePath());
            ras.write(fileData.getData());
            file.setLastModified(fileData.getLastModified());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileData prepareFileToSending(String filename){
        File file = getFileByName(filename);
        FileData fileData = null;
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
        byte[] data = new byte[(int) file.length()];
        ras.read(data);
        fileData = new FileData(AuthorizationHandler.getSessionCode(),filename,data,0,0, file.lastModified());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData;
    }

    public FileData prepareFileToSending(File file){
        FileData fileData = null;
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
            byte[] data = new byte[(int) file.length()];
            ras.read(data);
            fileData = new FileData(AuthorizationHandler.getSessionCode(),file.getName(),data,0,0, file.lastModified());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData;
    }

    public void updateDirectory() {
        currentDir = new File(Paths.get(currentDir.getPath()).toString());
        currentFiles = currentDir.listFiles();
    }

    public boolean isModifiedFile(FileImpl fileIMpl, long lastModified){
        File file = getFileByName(fileIMpl.getFileName());
        if (file.lastModified() == lastModified){
            log.info("FILE WASN't");
        } else {
            log.info("FILE WAS MODIFIED");
        }
        return file.lastModified() != lastModified;
    }


}
