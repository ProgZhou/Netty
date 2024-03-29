package com.java.netty.demo01.nettybasic.net3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/** pipeline测试
 * @author ProgZhou
 * @createTime 2022/05/30
 */
@Slf4j
public class PipelineTest {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //1. 通过channel获得pipeline
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        //2. 添加处理器，netty会自动添加一个head和一个tail handler，addLast是添加到tail之前head之后
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter(){
                            //InboundHandler 入站处理器，读取数据
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
                                log.debug("input data1...");
                                ByteBuf buf = (ByteBuf) msg;
                                String name = buf.toString(StandardCharsets.UTF_8);
                                super.channelRead(ctx , name);  //将当前处理好的数据交给下一个入站处理器
                            }
                        });
                        pipeline.addLast("h2", new ChannelInboundHandlerAdapter(){
                            //InboundHandler 入站处理器，读取数据
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
                                log.debug("input data2...");
                                //可以对接收到的数据进行进一步处理，比如封装成一个类
                                String name = msg.toString();
                                Student student = new Student(name);
                                super.channelRead(ctx , student);
                            }
                        });
                        pipeline.addLast("h3", new ChannelInboundHandlerAdapter(){
                            //InboundHandler 入站处理器，读取数据
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
                                log.debug("input data3...");
                                log.debug("最终数据：{}, 数据类型：{}", msg, msg.getClass());
//                                super.channelRead(ctx , msg);
                                nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("123".getBytes(StandardCharsets.UTF_8)));

                            }
                        });
                        //经过三次添加之后，pipeline中的处理链就变为：
                        //head <-> h1 <-> h2 <-> h3 <-> tail

                        //向pipeline中添加出站处理器，注意出站处理器只有经过写操作之后才会触发
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){
                            //添加出站处理器
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("output data4...");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h5", new ChannelOutboundHandlerAdapter(){
                            //添加出站处理器
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("output data5...");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h6", new ChannelOutboundHandlerAdapter(){
                            //添加出站处理器
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("output data6...");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }

                })
                .bind(8080);
    }

    @Data
    @AllArgsConstructor
    static class Student {
        private String name;
    }
}
