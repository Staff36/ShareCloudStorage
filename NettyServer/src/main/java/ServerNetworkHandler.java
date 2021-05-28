import DAO.UserDAO;
import DAO.UserDAOImplMySQL;
import Entities.User;
import MessageTypes.AuthorizationAnswer;
import MessageTypes.AuthorizationRequest;
import MessageTypes.FilesList;
import MessageTypes.ListFilesRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
@Log4j
public class ServerNetworkHandler {
    private static ServerNetworkHandler instance;
    private UserDAO<User> userDAO = new UserDAOImplMySQL();
    private List<User> users = new ArrayList<>();
    private File serversDirectory = Paths.get("C:\\test\\serversFiles").toFile();
    private File currentDirectory = Paths.get(serversDirectory.getAbsolutePath(), "Unnamed").toFile();
    private ServerNetworkHandler(){
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
        ServerBootstrap server = new ServerBootstrap();
        server.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        if(msg == null){
                                            return;
                                        }

                                        if (msg instanceof AuthorizationRequest){
                                            /*
                                            АУТЕНТИФИКАЦИЯ ПРОИСХОДИТ ТУТ
                                            */
                                            log.info("Auth trying");
                                            AuthorizationRequest request = (AuthorizationRequest) msg;
                                            User currentUser = userDAO.getInstanceByName(request.getLogin(), request.getPassword());
                                            AuthorizationAnswer answer;
                                            if (currentUser.getUser() == null ||
                                                     currentUser.getPassword() == null){
                                                answer = new AuthorizationAnswer("", "Incorrect login or password");
                                            } else {
                                                log.info("Success");
                                                users.add(currentUser);
                                                String code = String.valueOf(currentUser.getCode());
                                                answer = new AuthorizationAnswer(code,"Success");
                                            }
                                            ctx.writeAndFlush(answer);
                                        }
                                        if (msg instanceof ListFilesRequest){
                                            log.info("Request on list of files");
                                            ListFilesRequest lfr = (ListFilesRequest) msg;
                                            if (checkSessionCode(lfr.getSessionCode())){
                                                ctx.writeAndFlush(new FilesList(currentDirectory.listFiles()));
                                            }
                                        }
                                        if(msg instanceof String){
                                            log.info("String incoming " + msg);
                                            ctx.writeAndFlush("Server sought: " + msg);
                                        }
                                    }
                                });
                    }
                });

            ChannelFuture future = server.bind(9909).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ServerNetworkHandler getInstance(){
        if (instance == null){
            instance = new ServerNetworkHandler();
        }
        return instance;
    }

    public boolean checkSessionCode(String code){
        return users.stream().anyMatch(x -> Integer.parseInt(code) == x.getCode());
    }
}
