package com.java.netty.demo01.nettyadvanced.net3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/** 简单模拟redis协议
 * Redis在发送命令时需要遵守一个规定，比如命令 set name zhangsan
 * 需要先发送元素的个数，比如示例中有三个元素 set / name / zhangsan
 * *3
 * 然后后面需要跟每个元素的长度以及元素内容
 * $3
 * set
 * $4
 * name
 * $8
 * zhangsan
 *
 * @author ProgZhou
 * @createTime 2023/04/12
 */
@Slf4j
public class RedisProtocolTest {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        final byte[] LINE = {13, 10};
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("*3".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            buffer.writeBytes("$3".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            buffer.writeBytes("set".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            buffer.writeBytes("$4".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            buffer.writeBytes("name".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            buffer.writeBytes("$8".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            buffer.writeBytes("zhangsan".getBytes());
                            //每一条命令之间需要通过回车换行来区分
                            buffer.writeBytes(LINE);

                            ctx.writeAndFlush(buffer);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            log.info("{}", buf.toString(StandardCharsets.UTF_8));
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 6379).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

}
