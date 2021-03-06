package FrameControllers;

import Enums.Sides;
import MessageTypes.FileImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import java.io.IOException;

@Log4j
public class FrameSwitcher {
    public static void openFrame(String frame, String title){
        Parent parent;
        try {
            parent = FXMLLoader.load(FrameSwitcher.class.getResource(frame));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            log.error("Loading scene throw exception: ", e);
        }
    }
    public static void openRenameConfirmFrame(FileImpl file, Sides side, MainFrameController mfc){
        FXMLLoader loader = new FXMLLoader(FrameSwitcher.class.getResource("renameConfirmerFrame.fxml"));
        RenameConfirmController rcc = new RenameConfirmController(file, side, mfc);
        Parent parent;
        String title = file.isFile() ? "Share Cloud Storage: Rename file to ..."
                : "Share Cloud Storage: Rename directory to ...";
        loader.setController(rcc);
        try {
            parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            log.error("Loading scene throw exception: ", e);
        }
    }
    public static void openDeleteConfirmFrame(FileImpl file, Sides side, MainFrameController mfc){
        FXMLLoader loader = new FXMLLoader(FrameSwitcher.class.getResource("confirmDeleteFileFrame.fxml"));
        ConfirmDeleteFileController cdfc = new ConfirmDeleteFileController(file, side, mfc);
        Parent parent;
        String title = file.isFile() ? "Share Cloud Storage: Confirm delete file"
                : "Share Cloud Storage: Confirm delete directory";
        loader.setController(cdfc);
        try {
            parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            log.error("Loading scene throw exception: ", e);
        }
    }

    public static void openShareConfirmFrame(FileImpl file){
        FXMLLoader loader = new FXMLLoader(FrameSwitcher.class.getResource("shareConfirmFrame.fxml"));
        ShareConfirmController scc = new ShareConfirmController(file);
        Parent parent;
        String title = file.isFile() ? "Share Cloud Storage: Confirm share file"
                : "Share Cloud Storage: Confirm delete directory";
        loader.setController(scc);
        try {
            parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            log.error("Loading scene throw exception: ", e);
        }
    }

    public static void openMakeDirFrame(Sides side, MainFrameController mfc){
        FXMLLoader loader = new FXMLLoader(FrameSwitcher.class.getResource("mkDirConfirmFrame.fxml"));
        MkDirConfirmController mkdirController = new MkDirConfirmController(side,mfc);
        Parent parent;
        String title = "Creating directory";
        loader.setController(mkdirController);
        try {
            parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainFrameController.class.getResourceAsStream("icons/AppIcon.png")));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            log.error("Loading scene throw exception: ", e);
        }
    }


}
