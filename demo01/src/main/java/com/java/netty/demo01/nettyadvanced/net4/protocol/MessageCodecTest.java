package com.java.netty.demo01.nettyadvanced.net4.protocol;

import com.java.netty.demo01.nettyadvanced.net4.message.LoginRequestMessage;
import com.java.netty.demo01.nettyadvanced.net4.protocol.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/** 测试自定义消息编解码器MessageCodec
 * @author ProgZhou
 * @createTime 2023/04/17
 */
public class MessageCodecTest {

    public static void main(String[] args) throws Exception {
        //为避免在解码时出现黏包、半包现象，使用LengthFieldBasedFrameDecoder
        EmbeddedChannel channel = new EmbeddedChannel(new LengthFieldBasedFrameDecoder
                (1024, 12, 4, 0, 0)
                ,new LoggingHandler(), new MessageCodec());
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123456", "张三");
        //测试encode方法
        channel.writeOutbound(message);
        //测试decode方法
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);
        channel.writeInbound(buffer);
    }

}
