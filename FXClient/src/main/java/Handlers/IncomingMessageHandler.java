package Handlers;

import MessageTypes.AuthorizationAnswer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;
@AllArgsConstructor
public class IncomingMessageHandler extends ChannelInboundHandlerAdapter{

    Consumer<Object> mainCallBack;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg == null){
            return;
        }

        if(msg instanceof String){
            System.out.println(msg);

        }
        if (msg instanceof AuthorizationAnswer){
            AuthorizationAnswer answer = (AuthorizationAnswer) msg;
            System.out.println(answer.getStatus());
            AuthorizationHandler.checkAnswer(answer);
           mainCallBack.accept(answer.getStatus());
        }
    }
}
