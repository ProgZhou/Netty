package com.java.netty.demo01.nettybasic.net2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.java.netty.demo01.util.ByteBufferUtil.debugAll;

/** 多线程服务器
 * @author ProgZhou
 * @createTime 2022/05/24
 */
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        //这个selector专门监听accept事件
        Selector boss = Selector.open();
        SelectionKey bossKey = server.register(boss, SelectionKey.OP_ACCEPT, null);
        server.bind(new InetSocketAddress(8080));
        //1. 创建固定数量的worker并进行初始化
        Worker worker = new Worker("worker_0");
        // worker.register();

        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()) {
                    SocketChannel sc = server.accept();
                    sc.configureBlocking(false);
                    log.debug("client: {}", sc.getRemoteAddress());
                    //2. 关联selector，注意读写事件要关联到worker的selector上
                    log.debug("before read: {}", sc.getRemoteAddress());
                    worker.register();
                    sc.register(worker.selector, SelectionKey.OP_READ, null);
                    log.debug("after read: {}", sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false;  //表示线程还未初始化

        public Worker() {
        }

        public Worker(String name) {
            this.name = name;
        }

        //初始化线程和selector，这个方法只需要执行一遍即可，否则后续调用这个方法就会创建一个线程和一个selector、
        //一个worker只需要对应一个thread和selector即可
        public void register() throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                selector = Selector.open();
                thread.start();
                start = true;
            }
        }


        //worker线程专门处理读事件和写事件
        @Override
        public void run() {
            while(true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel sc = (SocketChannel) key.channel();
                            log.debug("reading: {}", sc.getRemoteAddress());
                            sc.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
