package com.java.netty.demo01.nettybasic.net2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/** channelFuture的关闭
 * @author ProgZhou
 * @createTime 2022/05/28
 */
@Slf4j
public class CloseFutureClient {
    /*
    * 现在有一个需求：客户端从控制台读入想要发送的数据给服务器，直到
    * 控制台输入"q"表示退出，关闭channel并处理后续的操作
    * */
    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                }).connect("localhost", 8080);

        Channel channel = channelFuture.sync().channel();
        log.debug("channel: {}", channel);
        //新开一个线程读取控制台的输入并发送给服务器
        new Thread(() -> {
            Scanner reader = new Scanner(System.in);

            while (true) {
                String line = reader.nextLine();
                if("q".equals(line)) {
                    channel.close();   //异步方法
                    break;
                }

                channel.writeAndFlush(line);
            }
        }, "input").start();

        //获取closeFuture对象，这个对象有两个作用：
        //1. 同步处理channel关闭之后的操作   2. 异步处理channel关闭之后的操作
        ChannelFuture closeFuture = channel.closeFuture();
        //同步处理，只要调用sync方法，就能阻塞main线程的执行，直到channel close之后继续执行
        //closeFuture.sync();
        //log.debug("处理关闭后的代码...");

        //异步方式与之前发送数据相同，使用addListener方法添加监听器
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("处理关闭之后的代码...");
                //关闭EventLoopGroup中的线程，使得整个客户端进程停止
                //shutdownGracefully(): 优雅地关闭线程，指允许线程再执行一段时间将未运行的代码运行完毕之后再停止
                group.shutdownGracefully();
            }
        });

    }
}
