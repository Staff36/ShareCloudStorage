package FrameControllers;

import Enums.Sides;
import Handlers.AuthorizationHandler;
import Handlers.FileHandler;
import Handlers.NetworkHandler;
import MessageTypes.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Log4j
public class MainFrameController implements Initializable {
    public ListView<String> clientsList;
    public TextField clientsPath;
    public Button clientsUp;
    public ListView<String> serversList;
    public Button mkServersDirButton;
    public Button sharButton;
    @Getter
    private FileHandler fileHandler;
    private String clientsListSelectedItem;
    private String serversListSelectedItem;
    private final Image file = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/fileIcon.png")));
    private final Image folder = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/dirIcon.png")));
    private final Image disc = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/driveIcon.png")));
    private final Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/imageIcon.png")));
    @Getter
    private NetworkHandler networkHandler;
    private Consumer<Object> callBack;
    @Getter
    private FileImpl[] serversFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileHandler = new FileHandler();
        callBack = o -> Platform.runLater(() -> handleIncomingMessage(o));
        networkHandler = NetworkHandler.getInstance();
        networkHandler.setMainCallBack(callBack);
        networkHandler.writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode(), ""));
        repaintClientsSide(File.listRoots());
    }

    public void selectItemOnClientsList(MouseEvent mouseEvent) {
        String currentItem = clientsList.getSelectionModel().getSelectedItem();
        if (currentItem == null) {
            return;
        }
        log.info("Clients Side selected item: " + currentItem);
        if (clientsListSelectedItem == null || !clientsListSelectedItem.equals(currentItem)) {
            clientsListSelectedItem = currentItem;
            return;
        }
        fileHandler.moveOrOpenFile(clientsListSelectedItem);
        repaintClientsSide(fileHandler.getCurrentFiles());
        clientsListSelectedItem = null;
    }

    public void selectItemOnServersList(MouseEvent mouseEvent) {
        String currentItem = serversList.getSelectionModel().getSelectedItem();
        log.info("Servers Side selected item: " + currentItem);
        if (serversListSelectedItem == null || !serversListSelectedItem.equals(currentItem)) {
            serversListSelectedItem = currentItem;
            updateShareButton();
            return;
        }
        FileImpl currentServersFile = Arrays.stream(serversFiles).filter(x -> x.getFileName().equals(currentItem)).findFirst().get();
        if (!currentServersFile.isFile()) {
            getRenewServersFilesList(currentItem);
            updateShareButton();
        }
        serversListSelectedItem = null;
    }

    public void repaintClientsSide(File[] files) {
        if (files == null) {
            return;
        }
        fileHandler.setCurrentFiles(files);
        clientsList.getItems().clear();
        clientsList.getItems().addAll(fileHandler.getListOfFileNames(files));
        clientsList.setCellFactory(this::updateClientsView);
    }

    public void repaintServersList() {

        List<String> fil = Arrays.stream(serversFiles).map(FileImpl::getFileName).collect(Collectors.toList());
        serversList.getItems().clear();
        serversList.getItems().addAll(fil);
        serversList.setCellFactory(this::updateServersView);
        serversList.refresh();
    }

    public void handleIncomingMessage(Object o) {
        if (o instanceof FilesList) {
            log.info("Updating serversList");
            FilesList filesList = (FilesList) o;
            serversFiles = filesList.getFiles();
            repaintServersList();
        }

        if (o instanceof FileData) {
            FileData fileData = (FileData) o;
            log.info("Downloading file, name: " + fileData.getName() + ", size: " + fileData.getData().length);
            if(fileData.getTotalPartsValue() > 0){
                fileHandler.downloadBigFile(fileData);
                if(fileData.getPart() == fileData.getTotalPartsValue()){
                    fileHandler.updateDirectory();
                    repaintClientsSide(fileHandler.getCurrentFiles());
                }
            } else{
                fileHandler.downloadRegularFile(fileData);
                fileHandler.updateDirectory();
                repaintClientsSide(fileHandler.getCurrentFiles());
            }
        }

        if (o instanceof MakeDirRequest){
            MakeDirRequest mkdir = (MakeDirRequest) o;
            fileHandler.makeDir(mkdir.getName());
        }

        if (o instanceof MovingToDirRequest){
            MovingToDirRequest movingToDir = (MovingToDirRequest) o;
            if (movingToDir.getDirName().equals("/GoToParent")){
                fileHandler.moveUp();
            } else {
            fileHandler.moveOrOpenFile(movingToDir.getDirName());
            }
        }
    }


    public void clientMoveToParent(ActionEvent actionEvent) {
        fileHandler.moveUp();
        repaintClientsSide(fileHandler.getCurrentFiles());
        clientsListSelectedItem = null;
    }

    public void serverMoveToParent(ActionEvent actionEvent) {
        getRenewServersFilesList("/parent");
        serversListSelectedItem = null;
    }


    public void getRenewServersFilesList(String filename) {
        networkHandler.writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode(), filename));
    }

    public void download(ActionEvent actionEvent) {
        if (serversList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        FileImpl file = Arrays.stream(serversFiles).filter(x->x.getFileName().equals(serversListSelectedItem)).findFirst().orElse(null);
        networkHandler.writeToChannel(new DownloadingRequest(AuthorizationHandler.getSessionCode(), serversListSelectedItem, file.isFile() ));

    }

    public void upload(ActionEvent actionEvent) {
        String filename = clientsList.getSelectionModel().getSelectedItem();
        if (filename == null) {
            return;
        }
        File file = fileHandler.getFileByName(filename);
        if (file.isDirectory()){
            fileHandler.uploadDirectory(file);
        } else {
            fileHandler.prepareFileToSending(file);
        }
        networkHandler.writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode(), ""));
    }

    public void deleteClientsFile(ActionEvent actionEvent) {
        String fileName = clientsList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        File currentFile = fileHandler.getFileByName(fileName);
        FileImpl currFile = new FileImpl(fileName, currentFile.list(), currentFile.isFile(), false, false);
        FrameSwitcher.openDeleteConfirmFrame(currFile, Sides.CLIENTS_SIDE, this);
    }

    public void createClientsDir(ActionEvent actionEvent) {

        FrameSwitcher.openMakeDirFrame(Sides.CLIENTS_SIDE, this);
        fileHandler.updateDirectory();
        repaintClientsSide(fileHandler.getCurrentFiles());
    }

    public void renameClientsFile(ActionEvent actionEvent) {
        String fileName = clientsList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        File currentFile = fileHandler.getFileByName(fileName);
        FileImpl fileImpl = new FileImpl(fileName, currentFile.list(), currentFile.isFile(), false, false);
        log.debug(fileImpl + "WILL BE RENAMED");
        FrameSwitcher.openRenameConfirmFrame(fileImpl, Sides.CLIENTS_SIDE, this);
    }

    public void renameServersFile(ActionEvent actionEvent) {
        String fileName = serversList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        FileImpl currentServersFile = Arrays.stream(serversFiles).filter(x -> x.getFileName().equals(fileName)).findFirst().get();
        FrameSwitcher.openRenameConfirmFrame(currentServersFile, Sides.SERVERS_SIDE, this);
    }

    public void deleteServersFile(ActionEvent actionEvent) {
        String fileName = serversList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        FileImpl currentServersFile = Arrays.stream(serversFiles).filter(x -> x.getFileName().equals(fileName)).findFirst().get();
        FrameSwitcher.openDeleteConfirmFrame(currentServersFile, Sides.SERVERS_SIDE, this);
    }

    public void createServersDir(ActionEvent actionEvent) {
        FrameSwitcher.openMakeDirFrame(Sides.SERVERS_SIDE, this);
    }

    private ListCell<String> updateServersView(ListView<String> param) {
        return new ListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(20);
                FileImpl itemsFIle = Arrays.stream(serversFiles).filter(x -> x.getFileName().equals(item)).findFirst().orElse(null);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item.endsWith(".png") || item.endsWith(".jpeg") || item.endsWith(".gif")) {
                        imageView.setImage(image);
                    } else if (itemsFIle != null && itemsFIle.isFile()) {
                        imageView.setImage(file);
                    } else {
                        imageView.setImage(folder);
                    }
                    setText(item);
                    setGraphic(imageView);
                }
            }
        };
    }

    public void synchronizeCurrentFolder(ActionEvent actionEvent) {
        if(clientsListSelectedItem == null){
            return;
        }
        File file = fileHandler.getFileByName(clientsListSelectedItem);
        if(file.isDirectory()) {
            networkHandler.writeToChannel(new SyncFolderRequest(new FileImpl(file.getName(), file.list(), false, false, false)));
        }
    }

    private ListCell<String> updateClientsView(ListView<String> param) {
        return new ListCell<String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(20);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item.endsWith(".png") || item.endsWith(".jpeg")) {
                        imageView.setImage(image);
                    } else if (fileHandler.getFileByName(item) != null && fileHandler.getFileByName(item).isFile()) {
                        imageView.setImage(file);
                    } else if (fileHandler.isRoot(item)) {
                        imageView.setImage(disc);
                    } else {
                        imageView.setImage(folder);
                    }
                    setText(item);
                    setGraphic(imageView);
                }
            }
        };
    }



    private void updateShareButton(){
        FileImpl file = Arrays.stream(serversFiles).filter(x -> x.getFileName().equals(serversListSelectedItem)).findFirst().orElse(null);
        if (file == null || !file.isFile() || file.isVirtualFile()){
            sharButton.setDisable(true);
            return;
        }
        if (file.isShared()){
            sharButton.setDisable(false);
            sharButton.setText("Unshare");
        } else {
            sharButton.setDisable(false);
            sharButton.setText("Share");
        }
    }


    public void shareFile(ActionEvent actionEvent) {
        if (serversListSelectedItem == null){
            return;
        }
        FileImpl file = Arrays.stream(serversFiles).filter(x->x.getFileName().equals(serversListSelectedItem)).findFirst().orElse(null);
        if (file.isShared()){

        }
        log.info("Sharing file is " + file.getFileName());
        FrameSwitcher.openShareConfirmFrame(file);
       networkHandler.writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode(), ""));
    }

    public void updateServersList(ActionEvent actionEvent) {
        networkHandler.writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode(), ""));
    }

    public void updateClientsList(ActionEvent actionEvent) {
        fileHandler.updateDirectory();
        repaintClientsSide(fileHandler.getCurrentFiles());
    }
}
