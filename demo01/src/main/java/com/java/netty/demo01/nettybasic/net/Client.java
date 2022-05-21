package com.java.netty.demo01.nettybasic.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/** 网络编程 --- 客户端
 * @author ProgZhou
 * @createTime 2022/05/21
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();
        //连接服务器
        client.connect(new InetSocketAddress("localhost", 8080));
        log.debug("waiting...");
    }
}
