package Handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryListener {
    private FileHandler fileHandler;
    private NetworkHandler networkHandler;


    public DirectoryListener(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        networkHandler = NetworkHandler.getInstance();
    }

    public void addListener(File file){
        WatchService watchService = null;
        try {
             watchService = FileSystems.getDefault().newWatchService();
             Path dir = Paths.get("C:\\src");
             dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
             boolean boll = true;
             while (boll){
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()){
                    System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                }
                boll = key.reset();
             }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
