import DAO.UserDAO;
import DAO.UserDAOImplMySQL;
import Entities.User;
import MessageTypes.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j;


@Log4j
public class IncomingMessageHandler extends ChannelInboundHandlerAdapter {
    private UserDAO<User> userDAO = new UserDAOImplMySQL();
    private FileHandler fileHandler = new FileHandler();

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("INCOMING MESSAGE!");
            if(msg == null){
                log.info("NULLABLE");
                return;
            }

            if (msg instanceof AuthorizationRequest){
                log.info("Auth trying");
                AuthorizationRequest request = (AuthorizationRequest) msg;
                User currentUser = userDAO.getInstanceByName(request.getLogin(), request.getPassword());
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
                log.info("Incoming a file, name " + fileData.getName() + ", size: " + fileData.getData().length);
                fileHandler.downloadFile(fileData);
                ctx.writeAndFlush(new FilesList(fileHandler.getListFiles()));
                return;
            }

            if (msg instanceof DownloadingRequest){
                log.info("FILEDOWNLOADREQUEST!");
                DownloadingRequest fdr = (DownloadingRequest) msg;
                FileData fileData = fileHandler.prepareFileToUploading(fdr.getFilename());
                log.info("Sending file, name: " + fileData.getName() + ", size: " + fileData.getData().length);
                ctx.writeAndFlush(fileData);
                return;
            }
            log.error("Unknown msg type: Class= " + msg.getClass().getCanonicalName() + " !");
        }

}
