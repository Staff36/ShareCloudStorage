package Handlers;

import java.io.File;

public class FileListener {
    private File file;
    private FileListener fileListener;
    private NetworkHandler networkHandler;


    public FileListener(FileListener fileListener, File file) {
        this.fileListener = fileListener;
        this.file = file;
        networkHandler = NetworkHandler.getInstance();
    }







}
