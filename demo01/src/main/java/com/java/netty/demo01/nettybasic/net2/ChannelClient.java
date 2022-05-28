package com.java.netty.demo01.nettybasic.net2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


/** Channel测试代码
 * @author ProgZhou
 * @createTime 2022/05/27
 */
@Slf4j
public class ChannelClient {
    public static void main(String[] args) throws InterruptedException {
        //2. 之后见到的带有Future，Promise类型的都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());

                    }
                })
                //1. connect方法连接到服务器，是个异步非阻塞方法
                //由main线程发起调用，真正执行connect的是nio线程，main线程可以继续向下执行
                .connect(new InetSocketAddress("localhost", 8080));
        //2.1 可以调用sync方法同步处理结果，主线程需要等待结果之后才能继续运行
//        channelFuture.sync();   //阻塞当前线程，直到nio线程连接建立完毕
//
//        Channel channel = channelFuture.channel();
//        //向服务器发送数据
//        channel.writeAndFlush("hello world");


        //2.2 使用addListener方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            //在nio线程连接建立好之后，会调用operationComplete
            @Override    //这个参数中的ChannelFuture和调用addListener的channelFuture是同一个
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("channel: {}", channel);
                channel.writeAndFlush("hello world");
            }
        });
    }
}
