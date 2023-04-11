package com.java.netty.demo01.nettyadvanced.net1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author ProgZhou
 * @createTime 2023/04/11
 */
@Slf4j
public class HelloWorldClient {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send();
        }
        log.info("数据发送完毕....");
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        //channel连接建立之后，触发Active事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //循环发送10次16歌字节的数据
//                            for (int i = 0; i < 10; i++) {
//                                ByteBuf buf = ctx.alloc().buffer(16);
//                                buf.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
//                                ctx.writeAndFlush(buf);
//                            }

//                                ByteBuf buf = ctx.alloc().buffer(16);
//                                buf.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
//                                ctx.writeAndFlush(buf);
//                                ctx.channel().close();

                            //发送固定长度的消息
                            log.debug("sending....");
                            Random random = new Random();
                            char c = '0';
                            ByteBuf buf = ctx.alloc().buffer();
                            for(int i = 0; i < 10; i++) {
                                byte[] bytes = new byte[10];
                                for(int j = 1; j <= random.nextInt(10) + 1; j++) {
                                    bytes[j] = (byte) c;
                                }
                                c++;
                                buf.writeBytes(bytes);
                            }
                            ctx.writeAndFlush(buf);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

}
