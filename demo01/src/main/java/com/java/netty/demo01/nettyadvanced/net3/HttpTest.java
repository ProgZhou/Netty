package com.java.netty.demo01.nettyadvanced.net3;

import com.sun.net.httpserver.HttpServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/** Http协议的解析
 * @author ProgZhou
 * @createTime 2023/04/12
 */
@Slf4j
public class HttpTest {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    //添加http的编解码器
                    ch.pipeline().addLast(new HttpServerCodec());
                    //添加自定义处理器处理经过http编码器之后的结果
//                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                        @Override
//                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            //输出经过编解码器之后，解析出来的类型
//                            log.debug("{}", msg.getClass());
//                        }
//                    });
                    //可以只关注请求头，专门处理请求头
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                            log.debug("{}", msg.uri());

                            //返回响应，有最基本的两个参数：协议版本和响应码
                            byte[] content = "<h1>Hello World</h1>".getBytes();
                            DefaultFullHttpResponse response =
                                    new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                            //添加响应头
                            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.length);
                            response.content().writeBytes(content);

                            //写回响应
                            ctx.writeAndFlush(response);
                        }
                    });
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
