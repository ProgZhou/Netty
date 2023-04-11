package com.java.netty.demo01.nettyadvanced.net2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/** 测试LTC解码器
 * @author ProgZhou
 * @createTime 2023/04/11
 */
public class LengthFieldDecoderTest {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );
        //报文格式：长度字段4个字节，然后是后续内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send("Hello, World", buffer);
        send("Hi", buffer);
        channel.writeInbound(buffer);
    }

    private static void send(String content, ByteBuf buffer) {
        //实际需要发送的内容
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);  //int是4个字节
        buffer.writeBytes(bytes);
    }

}
