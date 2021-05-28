import MessageTypes.AuthorizationAnswer;
import MessageTypes.AuthorizationRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.ArrayList;
import java.util.List;

public class ServerNetworkHandler {
    private static ServerNetworkHandler instance;
    List<User> users = new ArrayList<>();

    private ServerNetworkHandler(){
        users.add(new User("admin", "12345"));
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
                                            AuthorizationRequest request = (AuthorizationRequest) msg;
                                            System.out.println(request.getLogin() +" " + request.getPassword());
                                            User currentUser = users.stream().filter(user->
                                                    user.getUser().equals(request.getLogin()) &&
                                                            user.getPassword().equals(request.getPassword())
                                            ).findFirst().orElseGet(null);
                                            AuthorizationAnswer answer;
                                            if (currentUser == null){
                                                answer = new AuthorizationAnswer("", "Incorrect login or password");
                                            } else {
                                                String code = String.valueOf(currentUser.getCode());
                                                answer = new AuthorizationAnswer(code,"Success");
                                            }
                                            ctx.writeAndFlush(answer);
                                        }
                                        if(msg instanceof String){
                                            ctx.writeAndFlush("Server saught: " + msg);
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
}
