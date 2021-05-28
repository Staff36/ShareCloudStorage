package FrameControllers;

import Handlers.AuthorizationHandler;
import Handlers.FileHandler;
import Handlers.NetworkHandler;
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
    public ListView<String>  serversList;
    private FileHandler fileHandler;
    private String selectedItem;
    private Image file = new Image("https://svl.ua/image/cache/download_pdf-32x32.png");
    private Image folder = new Image("https://i0.wp.com/cdna.c3dt.com/icon/328326-com.jrdcom.filemanager.png?w=32");
    private Image disc = new Image("https://findicons.com/files/icons/998/airicons/32/hdd.png");
    private Image image = new Image("https://findicons.com/files/icons/1637/file_icons_vs_2/32/png.png");
    private NetworkHandler networkHandler;
    private Consumer<Object> callBack;
    private File[] serversFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileHandler = new FileHandler();
        callBack = o -> Platform.runLater(()->handleIncomingMessage(o));
        networkHandler = NetworkHandler.getInstance();
        networkHandler.setMainCallBack(callBack);
        repaintClientsSide(File.listRoots());

    }

    public void selectItemOnClientsList(MouseEvent mouseEvent) {
        String currentItem = clientsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !selectedItem.equals(currentItem)){
            selectedItem = currentItem;
            return;
        } else {
            fileHandler.moveOrOpenFile(selectedItem);
            repaintClientsSide(fileHandler.getCurrentFiles());
        }
    }


    public void repaintClientsSide(File[] files){
        if (files == null || files.length == 0){
            return;
        }
        fileHandler.setCurrentFiles(files);
        clientsList.getItems().clear();
        clientsList.getItems().addAll(fileHandler.getListOfFileNames(files));
        clientsList.setCellFactory(param -> new ListCell<String>(){
            private ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty){
                    setText(null);
                    setGraphic(null);
                } else {
                    if(item.endsWith(".png") || item.endsWith(".jpeg")){
                        imageView.setImage(image);
                    } else if (fileHandler.getFileByName(item)!= null && fileHandler.getFileByName(item).isFile()){
                        imageView.setImage(file);
                    } else if(fileHandler.isRoot(item)){
                        imageView.setImage(disc);
                    }else {
                        imageView.setImage(folder);
                    }
                    setText(item);
                    setGraphic(imageView);
                }
            }
        });
    }
    public void repaintServersList(File[] files){

        List<String> fil = Arrays.stream(serversFiles).map(x->x.getName()).collect(Collectors.toList());
        serversList.getItems().clear();
        serversList.getItems().addAll(fil);
        serversList.setCellFactory(param -> new ListCell<String>(){
            private ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                File itemsFIle = Arrays.stream(serversFiles).filter(x -> x.getAbsolutePath().equals(item) || x.getName().equals(item)).findFirst().orElse(null);
                if (empty){
                    setText(null);
                    setGraphic(null);
                } else {
                    if(item.endsWith(".png") || item.endsWith(".jpeg")){
                        imageView.setImage(image);
                    } else if (itemsFIle != null && itemsFIle.isFile()){
                        imageView.setImage(file);
                    } else {
                        imageView.setImage(folder);
                    }
                    setText(item);
                    setGraphic(imageView);
                }
            }
        });
        serversList.refresh();
    }

    public void handleIncomingMessage(Object o){
        log.info("Updating serversList");
        if (o instanceof FilesList){
            FilesList filesList = (FilesList) o;
            serversFiles = filesList.getFiles();
            repaintServersList(serversFiles);
        }
    }

    public void moveUp(ActionEvent actionEvent) {
        fileHandler.moveUp();
        repaintClientsSide(fileHandler.getCurrentFiles());
    }

    public void serverMoveToParent(ActionEvent actionEvent) {
        networkHandler.writeToChannel(new ListFilesRequest(AuthorizationHandler.getSessionCode()));
    }
}
