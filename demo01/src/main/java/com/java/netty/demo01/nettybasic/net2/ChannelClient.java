package com.java.netty.demo01.nettybasic.net2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static jdk.internal.net.http.common.Log.channel;

/** Channel测试代码
 * @author ProgZhou
 * @createTime 2022/05/27
 */
@Slf4j
public class ChannelClient {
    public static void main(String[] args) throws InterruptedException {
        //1. 客户端的启动器类
        final ChannelFuture channelFuture = new Bootstrap()
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
                .connect(new InetSocketAddress("localhost", 8080));
                channelFuture.sync();
        final Channel channel = channelFuture.channel();
        channel.writeAndFlush("hello world");
    }
}
