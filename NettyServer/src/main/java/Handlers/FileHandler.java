package Handlers;

import DAO.SharedFilesImplSQLite;
import MessageTypes.*;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@Log4j
public class FileHandler {
    private File parentDir = Paths.get("ServersStorage", "Unnamed").toFile();
    private File currentDir = parentDir;
    private String sessionCode;
    private List<File> synchronizedFolders;
    private File sharedFilesDirectory;
    private final static int PARTS_SIZE = 512 * 1024;

    public void initializeUser(String rootDir, String sessionCode){
       this.parentDir = Paths.get("ServersStorage", rootDir).toFile();
       if (!parentDir.exists()){
           parentDir.mkdir();
       }
       sharedFilesDirectory = Paths.get("ServersStorage", rootDir, "Shared Files").toFile();
       if(!sharedFilesDirectory.exists()){
           sharedFilesDirectory.mkdir();
       }
       currentDir = parentDir;
       this.sessionCode = sessionCode;
       synchronizedFolders = new ArrayList<>();
   }

   public File getFileByName(String name){
       return Arrays.stream(currentDir.listFiles()).filter(x->x.getName().equals(name)).findFirst().orElse(null);
   }

   public void addNewSynchroniseFolder(FileImpl file){

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

    public void downloadRegularFile(FileData fileData){
        File file = Paths.get(currentDir.toString(), fileData.getName()).toFile();
        log.info("Downloading file: " +file.getName());
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
         ras.write(fileData.getData());
         file.setLastModified(fileData.getLastModified());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            updateListsOfDirectories();
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
            log.debug("Writing success, file length is " + file.length()+ " now.");
            if (fileData.getPart() == fileData.getTotalPartsValue() - 1){
                file.setLastModified(fileData.getLastModified());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareFileToSending(File file, ChannelHandlerContext ctx){
        FileData fileData = null;
        log.debug("File length= " + file.length() + ", PARTS_SIZE = " + PARTS_SIZE);
        try(RandomAccessFile ras = new RandomAccessFile(file, "rw")){
            if (file.length() > PARTS_SIZE){

                int totalPartsValue = (int) Math.ceil(file.length() / (double) PARTS_SIZE);

                for (int currentPart = 0; currentPart < totalPartsValue; currentPart++) {
                    long currentPosition = (long) currentPart * PARTS_SIZE;
                    ras.seek(currentPosition);
                    int partsLength = (int) Math.min(file.length() - currentPosition, PARTS_SIZE);
                    byte[] bytes = new byte[partsLength];
                    ras.read(bytes);
                    log.info("Sending part # " + currentPart + "of " + (totalPartsValue - 1)+ " Big file: " + file.getName());
                    ctx.writeAndFlush(new FileData(sessionCode,file.getName(),bytes, currentPart, totalPartsValue, file.lastModified(), PARTS_SIZE));
                }
            } else{
                byte[] bytes = new byte[(int) file.length()];
                ras.read(bytes);
                log.info("Sending file: " + file.getName());
                ctx.writeAndFlush(new FileData(sessionCode,file.getName(),bytes, 0,0, file.lastModified(), (int) file.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void prepareFileToSending(String name, ChannelHandlerContext ctx){
        File file = Paths.get(currentDir.toString(), name).toFile();
         prepareFileToSending(file, ctx);
    }

    public FileImpl[] getListFiles() {
        return Arrays.stream(currentDir.listFiles())
                .map(x ->{
                        File file = SharedFilesImplSQLite.getSharableFileByName(x.getName(), parentDir);
                        return new FileImpl(x.getName(), x.list(), x.isFile(), file != null, currentDir.equals(sharedFilesDirectory));
                })
                .toArray(FileImpl[]::new);
    }

    public void makeDir(MakeDirRequest mdr){
        File file = Paths.get(currentDir.getPath(), mdr.getName()).toFile();
        log.info("Making dir: " + file.getAbsolutePath());
        file.mkdir();
        file.setLastModified(mdr.getLastModified());
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

    public void uploadDirectory(File file, ChannelHandlerContext ctx){
        log.info("Sending makeDir, and move There: " + file.getName());
        ctx.writeAndFlush(new MakeDirRequest("", file.getName(), file.lastModified()));
        ctx.writeAndFlush(new MovingToDirRequest("", file.getName()));
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()){
                uploadDirectory(file1, ctx);
            } else {
                log.info("Uploading file: " + file1.getName());
                prepareFileToSending(file1, ctx);
            }
        }
        ctx.writeAndFlush(new MovingToDirRequest("AuthorizationHandler.getSessionCode()", "/GoToParent"));
    }

}
