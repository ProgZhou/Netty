package com.java.netty.demo01.nettybasic.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/** netty入门 --- hello world客户端
 * @author ProgZhou
 * @createTime 2022/05/26
 */
@Slf4j
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //1. 客户端的启动器类
        new Bootstrap()
                //2. 添加EventLoop
                .group(new NioEventLoopGroup())
                //3. 选择客户端的channel实现
                .channel(NioSocketChannel.class)
                //4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override  //在连接建立之后被调用
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //5. 添加具体的处理器
                        nioSocketChannel.pipeline().addLast(new StringEncoder());

                    }
                })
                //6. 连接到服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel()
                //向服务器发送数据
                .writeAndFlush("hello world!");
    }
}
