package com.java.netty.demo01.nettybasic.net3;

import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/** Future测试
 * @author ProgZhou
 * @createTime 2022/05/30
 */
@Slf4j
public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Netty中的Future类似于jdk中的线程池，需要配合EventLoop使用
        NioEventLoopGroup group = new NioEventLoopGroup(2);

        io.netty.util.concurrent.Future<Integer> future = group.next().submit(() -> {
            log.debug("compute...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 70;
        });
        //Netty中的Future可以实现异步，由执行者去接收任务的执行结果
        future.addListener(future1 -> {
            log.debug("result: {}", future1.getNow());
        });
    }

    private static void jdkFutureTest() throws InterruptedException, ExecutionException {
        //jdk中的Future一般配合线程池使用
        ExecutorService pool = Executors.newFixedThreadPool(2);

        //提交任务，通过Future返回对象
        Future<Integer> future = pool.submit(() -> {
            log.debug("compute...");
            Thread.sleep(1000);
            return 50;
        });

        //主线程获取结果
        log.debug("waiting result...");
        log.debug("get: {}", future.get());
    }
}
