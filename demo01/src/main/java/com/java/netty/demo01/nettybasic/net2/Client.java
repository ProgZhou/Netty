package com.java.netty.demo01.nettybasic.net2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author ProgZhou
 * @createTime 2022/05/24
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();

        client.connect(new InetSocketAddress("localhost", 8080));

        client.write(StandardCharsets.UTF_8.encode("0123456789abcdef"));

        log.debug("waiting...");
    }
}
