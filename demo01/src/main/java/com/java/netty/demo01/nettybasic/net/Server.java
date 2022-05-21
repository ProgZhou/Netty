package com.java.netty.demo01.nettybasic.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.java.netty.demo01.util.ByteBufferUtil.debugRead;

/** 网络编程 --- 服务器
 * @author ProgZhou
 * @createTime 2022/05/21
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //添加selector解决非阻塞模式下的性能消耗问题

        //1. 创建selector，管理多个channel，这个demo下主要管理ServerSocketChannel和SocketChannel
        Selector selector = Selector.open();


        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        //2. 建立selector和channel之间的联系（专有名称：注册）
        //register()方法会返回一个SelectionKey，这个SelectionKey就是将来事件发生后，通过找到这个selectionKey，就可以知道是哪个channel发生的事件
        SelectionKey serverKey = server.register(selector, 0, null);
        log.debug("register key: {}", serverKey);

        //指明这个key只关注accept事件，上面register方法中的第二个参数的含义就是这个，0默认表示不关注任何事件
        serverKey.interestOps(SelectionKey.OP_ACCEPT);
        server.bind(new InetSocketAddress(8080));

        while(true) {
            //3. 调用selector的select方法，select方法当没有事件发生的时候，会让线程阻塞；当有事件发生，select方法才会恢复线程运行
            selector.select();

            //4. 处理事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();//selectedKeys方法拿到所有可用事件的集合，是一个Set集合
            while (iterator.hasNext()) {
                //拿到事件的key，拿着这个key去找到是哪个channel发生的事件
                SelectionKey key = iterator.next();
                log.debug("select key: {}", key);
                //通过key获得channel
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                //去处理事件
                SocketChannel accept = channel.accept();
                log.debug("socket: {}", accept);
            }

        }
    }

    private static void NioServerConnect() throws IOException {
        //使用nio阻塞模式编写服务器端的程序
        //0. 创建一个ByteBuffer接收数据
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1. 创建一个ServerSocketChannel作为服务器端
        ServerSocketChannel server = ServerSocketChannel.open();
        //将服务器端改成非阻塞模式
        server.configureBlocking(false);
        //2. 绑定一个端口，可以随意指定一个
        server.bind(new InetSocketAddress(8080));

        //3. 建立连接集合，用于处理多个客户端的连接
        List<SocketChannel> clients = new ArrayList<>();
        //接收连接
        while(true) {
            //4. 调用accept()方法建立与客户端的连接
            log.debug("connecting...");
            SocketChannel client = server.accept();   //阻塞方法，线程停止运行
            //将客户端的连接也置为非阻塞模式
            client.configureBlocking(false);
            log.debug("client {} has connected", client);
            //加入到连接集合中
            clients.add(client);
            //5. 接收客户端发送的数据
            for (SocketChannel socketChannel : clients) {
                socketChannel.read(buffer);    //阻塞方法，线程停止运行
                buffer.flip();
                debugRead(buffer);
                //清空buffer，为下一次写做准备
                buffer.clear();
                log.debug("read end... {}", socketChannel);
            }

        }
    }
}
