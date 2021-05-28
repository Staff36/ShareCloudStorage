package Handlers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.function.Consumer;
@Log4j
public class NetworkHandler {
    private static NetworkHandler instance;
    private static SocketChannel channel;
    private IncomingMessageHandler incomingMessageHandler = new IncomingMessageHandler();

    public void setMainCallBack(Consumer<Object> mainCallBack) {
        this.mainCallBack = mainCallBack;
        incomingMessageHandler.setCallBack(mainCallBack);
    }

    @Setter
    private Consumer<Object> mainCallBack;
    private NetworkHandler(){
        Thread thread = new Thread(() -> {
            EventLoopGroup elg = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(elg)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(), incomingMessageHandler);
                            }
                        });
                ChannelFuture f = bootstrap.connect("localhost", 9909).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                elg.shutdownGracefully();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void writeToChannel(Object o){
        log.info("Sending message " + o);
        channel.writeAndFlush(o);
    }

    public static NetworkHandler getInstance(){
        if (instance == null){
            instance = new NetworkHandler();
        }
        return instance;
    }



}
