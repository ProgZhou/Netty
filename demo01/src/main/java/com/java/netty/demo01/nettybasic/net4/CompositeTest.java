package com.java.netty.demo01.nettybasic.net4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.java.netty.demo01.nettybasic.net4.ByteBufTest1.log;

/** 零拷贝方法composite
 * @author ProgZhou
 * @createTime 2023/04/11
 */
public class CompositeTest {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        //使用composite逻辑组合两个byteBuf
        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true, buf1, buf2);
        log(buffer);
    }

}
