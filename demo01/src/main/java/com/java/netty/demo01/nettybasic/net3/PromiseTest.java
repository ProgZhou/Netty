package com.java.netty.demo01.nettybasic.net3;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/** netty Promise测试
 * @author ProgZhou
 * @createTime 2022/05/30
 */
@Slf4j
public class PromiseTest {
    public static void main(String[] args) {

        //1. 准备一个EventLoop
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();

        //2. 可以主动创建promise对象，存储结果的容器
        Promise<Integer> promise = new DefaultPromise<>(eventLoop);

        //3. 由任意一个线程去执行计算任务，计算结束之后，向promise中填充结果
        eventLoop.execute(() -> {
            log.debug("begin...");
            try {
                int i = 1 / 0;
                Thread.sleep(1000);
            } catch (Exception e) {
//                e.printStackTrace();
                //promise还可以存储执行异常的结果，当执行任务过程中出现异常，可以向promise中存储一个异常，表明执行过程中出现异常
                promise.setFailure(e);
            }
            //如果执行成功，则放入正确的执行结果
            promise.setSuccess(80);
        });

        //4. 接收结果，可以同步地使用get()方法接收结果，也可以使用addListener()异步接收结果
        log.debug("waiting...");
        try {
            log.debug("result: {}", promise.get());
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("exception...");
        }
    }
}
