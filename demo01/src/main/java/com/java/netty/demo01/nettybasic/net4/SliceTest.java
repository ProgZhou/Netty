package com.java.netty.demo01.nettybasic.net4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.java.netty.demo01.nettybasic.net4.ByteBufTest1.log;

/** 测试ByteBuf slice
 * @author ProgZhou
 * @createTime 2023/04/11
 */
public class SliceTest {

    public static void main(String[] args) {
        //1. 创建ByteBuf
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);

        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});

        log(buf);
        //2. 对ByteBuf进行切片， 在切片过程中并没有发生数据的复制
        ByteBuf slice1 = buf.slice(0, 5);
        ByteBuf slice2 = buf.slice(5, 5);

        log(slice1);
        log(slice2);

        //3. 对切片中的数据进行修改，验证与原始ByteBuf使用的是同一份内存
        slice1.setByte(0, 'b');
        log(slice1);
        log(buf);

    }

}
