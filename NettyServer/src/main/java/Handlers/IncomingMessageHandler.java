package Handlers;

import DAO.DAO;
import DAO.UserDAOImplMySQL;
import Entities.User;
import MessageTypes.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j;


@Log4j
public class IncomingMessageHandler extends ChannelInboundHandlerAdapter {
    private DAO<User> DAO = new UserDAOImplMySQL();
    private FileHandler fileHandler = new FileHandler();

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg == null){
                log.info("Nullable message");
                return;
            }

            if (msg instanceof AuthorizationRequest){
                log.info("Auth trying");
                AuthorizationRequest request = (AuthorizationRequest) msg;
                User requestUser = new User();
                requestUser.setUser(request.getLogin());
                requestUser.setPassword(request.getPassword());
                User currentUser = DAO.getInstanceByName(requestUser);
                AuthorizationAnswer answer;
                if (currentUser.getUser() == null){
                    log.info("Incorrect trying to Authorization, user not found in DB");
                    answer = new AuthorizationAnswer("", "Incorrect login or password");
                } else {
                    log.info("Successfully authorization. Login: " + currentUser.getUser() + ", Directory: " + currentUser.getRootDir());
                    String code = String.valueOf(currentUser.getCode());
                    fileHandler.initializeUser(currentUser.getRootDir(), code);
                    answer = new AuthorizationAnswer(code,"Success");
                }
                ctx.writeAndFlush(answer);
                return;
            }

            if (msg instanceof ListFilesRequest){
                ListFilesRequest lfr = (ListFilesRequest) msg;
                log.info("msg is " + lfr.getFilename());
                if (lfr.getFilename().equals("/parent")){
                    fileHandler.moveToParentDirectory();
                } else if (lfr.getFilename().equals("")){

                } else {
                    fileHandler.moveToDirectory(lfr.getFilename());
                }
                log.info("Sending list of files from Directory: " + fileHandler.getCurrentDir().getAbsolutePath());
                ctx.writeAndFlush(new FilesList(fileHandler.getListFiles()));
                return;
            }

            if (msg instanceof FileData){
                FileData fileData = (FileData) msg;
                log.info("Incoming file, name: " + fileData.getName() + ", size: " + fileData.getData().length);
                fileHandler.downloadFile(fileData);
                ctx.writeAndFlush(new FilesList(fileHandler.getListFiles()));
                return;
            }

            if (msg instanceof DownloadingRequest){
                DownloadingRequest fdr = (DownloadingRequest) msg;
                FileData fileData = fileHandler.prepareFileToUploading(fdr.getFilename());
                log.info("Sending file, name: " + fileData.getName() + ", size: " + fileData.getData().length);
                ctx.writeAndFlush(fileData);
                return;
            }

            if (msg instanceof RenameFileRequest){
                RenameFileRequest rfr = (RenameFileRequest) msg;
                if (rfr.getOldFile() == null || rfr.getNewFile() == null){
                    return;
                }
                log.info("Renaming file: " + rfr.getOldFile().getName() + ", to  " + rfr.getNewFile().getName());
                fileHandler.renameFile(rfr);
            }

            if (msg instanceof DeleteFileRequest){
                DeleteFileRequest dfr = (DeleteFileRequest) msg;
                fileHandler.deleteFile(dfr);
            }

            if (msg instanceof MakeDirRequest){
                MakeDirRequest mdr = (MakeDirRequest) msg;
                log.info("Making directory " + mdr.getName());
                fileHandler.makeDir(mdr.getName());
                return;
            }

            if (msg instanceof MovingToDirRequest){
                MovingToDirRequest movingToDirRequest = (MovingToDirRequest) msg;
                log.info("message folder: " + movingToDirRequest.getDirName());
                log.info(movingToDirRequest.getDirName().equals("/GoToParent"));
                if (movingToDirRequest.getDirName().equals("/GoToParent")){
                    log.info("Moving to parent directory");
                    fileHandler.moveToParentDirectory();
                } else {
                    fileHandler.moveToDirectory(movingToDirRequest.getDirName());
                }
                return;
            }

            log.error("Unknown msg type: Class= " + msg.getClass().getCanonicalName() + " !");
        }

}
