package com.java.netty.demo01.nettybasic.net2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

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

        //优化成多个线程的worker Runtime.getRuntime().availableProcessors()方法获取机器cpu的核心数
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
//        //1. 创建固定数量的worker并进行初始化
//        Worker worker = new Worker("worker_0");

        //设置一个计数器
        AtomicInteger index = new AtomicInteger();
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
                   //多线程模式，使用轮询的方式将任务均匀分摊到多个线程下
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    // worker.register(sc);
//                    sc.register(worker.selector, SelectionKey.OP_READ, null);
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
        //用于线程间传递消息
        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();


        public Worker() {
        }

        public Worker(String name) {
            this.name = name;
        }

        //初始化线程和selector，这个方法只需要执行一遍即可，否则后续调用这个方法就会创建一个线程和一个selector、
        //一个worker只需要对应一个thread和selector即可
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                selector = Selector.open();
                thread.start();
                start = true;
            }

            //将要执行的任务添加到队列中，任务并没有立即执行
            queue.add(() -> {
                try {
                    sc.register(this.selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            //唤醒selector，防止selector一直阻塞
            selector.wakeup();
        }


        //worker线程专门处理读事件和写事件
        @Override
        public void run() {
            while(true) {
                try {
                    selector.select();
                    //取出任务
                    Runnable task = queue.poll();
                    if(task != null) {
                        //执行任务，这就实现了sc.register(worker.selector, SelectionKey.OP_READ, null);与selector.select()在同一个线程中执行
                        task.run();
                    }
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
