package com.java.netty.demo01.nio.net1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/** 处理写事件的服务器端
 * @author ProgZhou
 * @createTime 2022/05/24
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        //设置为非阻塞模式
        server.configureBlocking(false);
        //创建selector
        Selector selector = Selector.open();

        SelectionKey serverKey = server.register(selector, SelectionKey.OP_ACCEPT, null);

        server.bind(new InetSocketAddress(8080));

        while(true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()) {
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);

                    SelectionKey clientKey = client.register(selector, 0, null);
                    log.debug("client: {}", client);

                    //1. 向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 5000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                    int write = client.write(buffer);
                    log.debug("write: {}", write);
                    //将while循环改成if条件判断
                    if(buffer.hasRemaining()) {
                        //如果一次性并没有写完数据，则在clientKey上关联一个可写事件，但注意不能将之前key上关注的事件覆盖掉
                        clientKey.interestOps(clientKey.interestOps() + SelectionKey.OP_WRITE);
                        //把未写完的buffer附加到key上
                        clientKey.attach(buffer);
                    }

                    //当数据量很大时，并不能一次性写完，所以使用while循环逐步写入数据
//                    while(buffer.hasRemaining()) {
//                        //2. write的返回值代表实际写入的字节数
//                        int write = client.write(buffer);
//                        log.debug("write: {}", write);
//                    }
                } else if (key.isWritable()) {
                    ByteBuffer b = (ByteBuffer) key.attachment();
                    //获取触发事件的channel
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(b);
                    log.debug("write: {}", write);

                    //如果写完了，清理bytebuffer
                    if(!b.hasRemaining()) {
                        key.attach(null);   //使用null值覆盖掉之前附加的buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);  //可以把与写事件的关联也去掉

                    }
                }
            }
        }
    }
}
