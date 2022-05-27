package com.java.netty.demo01.nettybasic.net2;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**EventLoop基本使用
 * @author ProgZhou
 * @createTime 2022/05/26
 */
@Slf4j
public class EventLoopTest {
    public static void main(String[] args) {
        //1. 创建事件循环组，一个实现类
        //如果没给参数，默认是CPU的核心数
        EventLoopGroup group = new NioEventLoopGroup(2);   //NioEventLoopGroup是一个常见的实现类，能够处理ip事件，普通任务，定时任务

        log.debug("core: {}", NettyRuntime.availableProcessors());

        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

//        group.next().execute(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.debug("print...");
//        });

        //执行定时任务
        group.next().scheduleWithFixedDelay(() -> {
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);


        log.debug("main..");

    }
}
