package com.java.netty.demo01.util;

import java.nio.ByteBuffer;

import static com.java.netty.demo01.util.ByteBufferUtil.debugAll;

/** 解决半包黏包问题的工具包
 * @author ProgZhou
 * @createTime 2022/05/23
 */
public abstract class SplitSolve {
    //实现split方法分离接收的信息
    public static void split(ByteBuffer source) {
        //读模式
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //计算一条消息的长度
            int length = i + 1 - source.position();
            //寻找一条消息与下一条消息的分隔符
            if (source.get(i) == '\n') {
                ByteBuffer target = ByteBuffer.allocate(length);
                //向缓冲区中写入数据
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }

        //注意这里需要使用compact方法，因为上一次读取的信息很可能有半包现象的遗留，下次写的时候需要从上次未读部分写起
        source.compact();
    }
}
