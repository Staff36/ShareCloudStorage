package Handlers;

import FrameControllers.MainFrameController;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryListener implements Runnable{
    private FileHandler fileHandler;
    private NetworkHandler networkHandler;
    private File listenedDirectory;
    public DirectoryListener(File listenedDirectory, MainFrameController mfc) {
        this.fileHandler = fileHandler;
        this.listenedDirectory = listenedDirectory;
        networkHandler = NetworkHandler.getInstance();
    }

    @Override
    public void run() {
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path dir = listenedDirectory.toPath();
            dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            boolean boll = true;
            while (boll){
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()){

                }
                boll = key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
