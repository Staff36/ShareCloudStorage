package Handlers;

import MessageTypes.AuthorizationAnswer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.util.function.Consumer;
@Log4j
@Data
public class IncomingMessageHandler extends ChannelInboundHandlerAdapter{

    private Consumer<Object> callBack;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    log.info(msg.getClass().getCanonicalName() + "IS INCOMING");
        if (msg == null){
            return;
        }

        if(msg instanceof String){
            log.info(msg);
            return;
        }
        if (msg instanceof AuthorizationAnswer){

            AuthorizationAnswer answer = (AuthorizationAnswer) msg;
            log.info(answer.getStatus());
            AuthorizationHandler.checkAnswer(answer);
            callBack.accept(answer.getStatus());
            return;
        }

            callBack.accept(msg);


    }
}
