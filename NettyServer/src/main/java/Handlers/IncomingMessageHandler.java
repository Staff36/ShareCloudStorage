package Handlers;

import DAO.UserDAOImplSQLite;
import DAO.ConfirmEmailSQLite;
import Entities.Confirmation;
import Entities.User;
import MessageTypes.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j;
import java.util.Random;


@Log4j
public class IncomingMessageHandler extends ChannelInboundHandlerAdapter {
    private FileHandler fileHandler = new FileHandler();
    private MailSender mailSender = new MailSender();
    private final Random random = new Random();

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
                User currentUser = UserDAOImplSQLite.getInstanceByName(requestUser);
                AuthorizationAnswer answer;
                if (currentUser.getUser() == null){
                    log.info("Incorrect trying to Authorization, user not found in DB");
                    answer = new AuthorizationAnswer("", "Incorrect login or password");
                } else if (!currentUser.isEmailIsConfirmed()){
                    log.info("Email didn't confirmed");
                    answer = new AuthorizationAnswer("", "Email didn't confirmed");
                } else{
                    log.info("Successfully authorization. Login: " + currentUser.getUser() + ", Directory: " + currentUser.getRootDir());
                    String code = String.valueOf(currentUser.getCode());
                    fileHandler.initializeUser(currentUser.getRootDir(), code);
                    answer = new AuthorizationAnswer(code,"Success");
                }
                ctx.writeAndFlush(answer);
                return;
            }

            if(msg instanceof RegistrationRequest){
                RegistrationRequest request = (RegistrationRequest) msg;
                User user = new User();
                user.setUser(request.getLogin());
                user.setPassword(request.getPassword());
                user.setEmail(request.getEMail());
                user.setRootDir(request.getLogin());
                User userFromDB = UserDAOImplSQLite.getInstanceByName(user);
                if (userFromDB.getUser() != null){
                    log.info("User already exists");
                    ctx.writeAndFlush(new RegistrationAnswer("User already exists"));
                    return;
                }
                UserDAOImplSQLite.create(user);
                int confirmationCode = random.nextInt(899999) +100000;
                Confirmation confirmation = new Confirmation();
                confirmation.setCode(confirmationCode);
                confirmation.setEmail(user.getEmail());
                ConfirmEmailSQLite.create(confirmation);
                String text = "Confirm your e-mail, your code is: " + confirmationCode;
                log.info("Conf code is " + confirmationCode);
                mailSender.send("Share CloudStorage: confirm your e-mail", text, request.getEMail());
                ctx.writeAndFlush(new RegistrationAnswer("Success"));
                return;
            }

            if (msg instanceof ConformationRequest){
                ConformationRequest conformationRequest = (ConformationRequest) msg;
                Confirmation confirmation = new Confirmation();
                confirmation.setEmail(conformationRequest.getEmail());
                confirmation.setCode(conformationRequest.getCode());
                log.info("Code is: " + confirmation.getCode() + "  Email is: " + confirmation.getEmail());
                Confirmation returnedConf = ConfirmEmailSQLite.getInstanceByName(confirmation);
                log.info("Code is: " + returnedConf.getCode() + "  Email is: " + returnedConf.getEmail());
                if (returnedConf.getEmail() == null){
                    log.info("Error: wrong code: ");
                    ctx.writeAndFlush(new ConfirmationAnswer("Error"));
                    return;
                }
                ConfirmEmailSQLite.confirmEmail(returnedConf);
                log.info("Success");
                ctx.writeAndFlush(new ConfirmationAnswer("Success"));
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
                log.info("Renaming file: " + rfr.getOldFile().getFileName() + ", to  " + rfr.getNewFile().getFileName());
                fileHandler.renameFile(rfr);
            }

            if (msg instanceof DeleteFileRequest){
                DeleteFileRequest dfr = (DeleteFileRequest) msg;
                fileHandler.deleteFile(dfr);
            }

            if (msg instanceof MakeDirRequest){
                MakeDirRequest mdr = (MakeDirRequest) msg;
                log.info("Making directory " + mdr.getName());
                fileHandler.makeDir(mdr);
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
