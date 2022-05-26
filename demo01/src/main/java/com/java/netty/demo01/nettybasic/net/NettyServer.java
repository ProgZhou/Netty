package com.java.netty.demo01.nettybasic.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/** netty入门 --- hello world服务器端
 * @author ProgZhou
 * @createTime 2022/05/26
 */
@Slf4j
public class NettyServer {
    public static void main(String[] args) {
        //1. ServerBootstrap: 启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2. EventLoop：类似于之前nio中boss和worker，一个EventLoop中包含了一个selector和一个thread
                //group表示组
                .group(new NioEventLoopGroup())
                //3. 选择服务器的ServerSocketChannel实现 netty支持多种ServerSocketChannel的实现，这里选择一个比较通用的
                .channel(NioServerSocketChannel.class)
                //4. 之前写的nio方式的worker专门负责处理读事件和写事件，这个childHandler方法就是决定worker能执行哪些操作
                .childHandler(
                        //5. channel是和客户端进行读写的通道   Initializer，初始化器，负责添加别的handler
                        //这个初始化器会在连接建立之后被调用
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //6. 添加具体的handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder());  //字符串解码
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {  //自定义handler
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("msg: {}", msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
