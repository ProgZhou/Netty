package com.java.netty.demo01.nio.net1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/** 客户端发送数据
 * @author ProgZhou
 * @createTime 2022/05/24
 */
@Slf4j
public class Cilent {
    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();

        log.debug("connecting...");
        client.connect(new InetSocketAddress("localhost", 8080));

        //接收服务器端发送的数据
        int count = 0;   //记录每次接收数据的实际大小
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        while(true) {
            count += client.read(buffer);
            log.debug("read: {}", count);
            buffer.clear();
        }
    }
}
