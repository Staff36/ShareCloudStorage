package FrameControllers;

import Enums.Sides;
import Handlers.AuthorizationHandler;
import Handlers.FileHandler;
import Handlers.NetworkHandler;
import MessageTypes.FileData;
import MessageTypes.DownloadingRequest;
import MessageTypes.FilesList;
import MessageTypes.ListFilesRequest;
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
    private File[] serversFiles;


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
            return;
        }

        File currentServersFile = Arrays.stream(serversFiles).filter(x -> x.getName().equals(currentItem)).findFirst().get();
        if (currentServersFile.isDirectory()) {
            getRenewServersFilesList(currentItem);
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
        List<String> fil = Arrays.stream(serversFiles).map(File::getName).collect(Collectors.toList());
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
            fileHandler.downloadFile(fileData);
            fileHandler.updateDirectory();
            repaintClientsSide(fileHandler.getCurrentFiles());
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
        networkHandler.writeToChannel(new DownloadingRequest(AuthorizationHandler.getSessionCode(), serversList.getSelectionModel().getSelectedItem()));

    }

    public void upload(ActionEvent actionEvent) {
        if (clientsList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        networkHandler.writeToChannel(fileHandler.prepareFileToSending(clientsList.getSelectionModel().getSelectedItem()));

    }

    public void deleteClientsFile(ActionEvent actionEvent) {
        String fileName = clientsList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        File currentFile = fileHandler.getFileByName(fileName);
        currentFile.delete();
        fileHandler.updateDirectory();
        repaintClientsSide(fileHandler.getCurrentFiles());
    }

    public void createClientsDir(ActionEvent actionEvent) {
        String fileName = clientsList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        // TODO: 01.06.2021 (create new frame with name of folder)
    }

    public void renameClientsFile(ActionEvent actionEvent) {
        String fileName = clientsList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        FrameSwitcher.openRenameConfirmFrame(fileHandler.getFileByName(fileName), Sides.CLIENTS_SIDE);
        fileHandler.updateDirectory();
        repaintClientsSide(fileHandler.getCurrentFiles());
    }

    public void renameServersFile(ActionEvent actionEvent) {
        String fileName = serversList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        File currentServersFile = Arrays.stream(serversFiles).filter(x -> x.getName().equals(fileName)).findFirst().get();
        FrameSwitcher.openRenameConfirmFrame(currentServersFile, Sides.SERVERS_SIDE);
        getRenewServersFilesList("");
    }

    public void deleteServersFile(ActionEvent actionEvent) {
        String fileName = clientsList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        FrameSwitcher.openDeleteConfirmFrame(fileHandler.getFileByName(fileName), Sides.CLIENTS_SIDE);
    }

    public void createServersDir(ActionEvent actionEvent) {
        String fileName = serversList.getSelectionModel().getSelectedItem();
        if (fileName == null) {
            return;
        }
        File currentServersFile = Arrays.stream(serversFiles).filter(x -> x.getName().equals(fileName)).findFirst().get();
        FrameSwitcher.openRenameConfirmFrame(currentServersFile, Sides.SERVERS_SIDE);
    }

    private ListCell<String> updateServersView(ListView<String> param) {
        return new ListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(20);
                File itemsFIle = Arrays.stream(serversFiles).filter(x -> x.getAbsolutePath().equals(item) || x.getName().equals(item)).findFirst().orElse(null);
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
        // TODO: 01.06.2021 create ChangeListener on File
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
}
