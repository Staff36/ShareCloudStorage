package Handlers;

import MessageTypes.FileData;
import MessageTypes.FileImpl;
import MessageTypes.MakeDirRequest;
import MessageTypes.MovingToDirRequest;
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
    private final static int PARTS_SIZE =512 * 1024;
    private List<DirectoryListener> listenedDirectories = new ArrayList<>();
    private File currentDir = File.listRoots()[0];
    private File[] currentFiles;
    private NetworkHandler networkHandler = NetworkHandler.getInstance();

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
    public void downloadRegularFile(FileData fileData){
        File file = Paths.get(currentDir.toString(), fileData.getName()).toFile();
        try (RandomAccessFile ras = new RandomAccessFile(file,"rw")){
            log.info("Writing file: " + fileData.getName() + " into " + file.getAbsolutePath());
            ras.write(fileData.getData());
            file.setLastModified(fileData.getLastModified());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadBigFile(FileData fileData) {
        File file = Paths.get(currentDir.toString(), fileData.getName()).toFile();
        if (file.exists() && fileData.getPart() == 0){
            file.delete();
        }
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
            long position = (long) fileData.getPart() * fileData.getPartsSize();
            ras.seek(position);
            ras.write(fileData.getData());
            log.debug("Writing success, file length is " + file.length() + " now.");
            if (fileData.getPart() == fileData.getTotalPartsValue() - 1){
                file.setLastModified(fileData.getLastModified());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareFileToSending(File file){
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
            if (file.length() > PARTS_SIZE) {
                int totalPartsValue = (int) Math.ceil(file.length() / (double) PARTS_SIZE);
                for (int currentPart = 0; currentPart < totalPartsValue; currentPart++) {
                    long currentPosition = (long) currentPart * PARTS_SIZE;
                    ras.seek(currentPosition);
                    int partsLength = (int) Math.min(file.length() - currentPosition, PARTS_SIZE);
                    byte[] bytes = new byte[partsLength];
                    ras.read(bytes);
                    log.info("Sending part # " + currentPart + " of " + (totalPartsValue - 1) + " Big file: " + file.getName());
                    networkHandler.writeToChannel(new FileData(AuthorizationHandler.getSessionCode(), file.getName(), bytes, currentPart, totalPartsValue, file.lastModified(), PARTS_SIZE));
                }
            }else {
                byte[] data = new byte[(int) file.length()];
                ras.read(data);
                networkHandler.writeToChannel(new FileData(AuthorizationHandler.getSessionCode(),file.getName(),data,0,0, file.lastModified(), (int) file.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void uploadDirectory(File file){
        NetworkHandler networkHandler = NetworkHandler.getInstance();
        log.info("Sending makeDir, and move There: " + file.getName());
        networkHandler.writeToChannel(new MakeDirRequest(AuthorizationHandler.getSessionCode(), file.getName(), file.lastModified()));
        networkHandler.writeToChannel(new MovingToDirRequest(AuthorizationHandler.getSessionCode(), file.getName()));
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()){
                uploadDirectory(file1);
            } else {
                log.info("Uploading file: " + file1.getName());
                prepareFileToSending(file1);
            }
        }
        networkHandler.writeToChannel(new MovingToDirRequest(AuthorizationHandler.getSessionCode(), "/GoToParent"));
    }


    public void makeDir(String name) {
        File file = Paths.get(currentDir.getPath(), name).toFile();
        if (Arrays.stream(currentDir.listFiles()).anyMatch(x-> x.getName().equals(name))){
         return;
        }
        file.mkdir();
    }
}
