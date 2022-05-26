package com.java.netty.demo01.nio.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

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
        client.write(StandardCharsets.UTF_8.encode("0123456789abcdef333\n"));
        log.debug("waiting...");
    }
}
