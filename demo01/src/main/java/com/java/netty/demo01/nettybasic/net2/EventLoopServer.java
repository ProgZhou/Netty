package com.java.netty.demo01.nettybasic.net2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/** EventLoop处理io事件
 * @author ProgZhou
 * @createTime 2022/05/27
 */
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        //细分2：专门创建一个EventLoopGroup来处理时间较长的事件
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                //更加明确地分工，可以传两个NioEventLoopGroup
                //细分1：第一个EventLoopGroup专门处理accept事件    第二个EventLoopGroup专门处理读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
//                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override      //如果没有使用StringDecoder，传过来的就是ByteBuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                //输出
                                log.debug("msg: {}", buf.toString(StandardCharsets.UTF_8));
                                //将消息传递给下一个handler
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(group, "handler2",new ChannelInboundHandlerAdapter() {
                            @Override      //如果没有使用StringDecoder，传过来的就是ByteBuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                //输出
                                log.debug("msg: {}", buf.toString(StandardCharsets.UTF_8));
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
