package com.java.netty.demo01.nettybasic.net4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @author ProgZhou
 * @createTime 2023/04/09
 */
//@Slf4j
public class ByteBufTest1 {
    public static void main(String[] args) {

        //1. 创建ByteBuf，Netty中的ByteBuf可以动态扩容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();  //可以指定容量，也可以不指定容量
        System.out.println(buf);
        //2. 测试ByteBuf的动态扩容
//        log.info("{}", buf);
        log(buf);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 300; i++) {   //默认的bytebuf容量是256
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
//        log.info("{}", buf);
        log(buf);
    }

    @Test
    public void testMethod() {
        //指定10个字节的ByteBuf
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);

        //写入字节数组
        buf.writeBytes(new byte[]{1, 2, 3, 4});
//        log(buf);

        //写入整型，4个字节，大端写入
        buf.writeInt(5);
//        log(buf);

        buf.writeInt(6);
//        log(buf);

        System.out.println(buf.readByte());
        System.out.println(buf.readByte());
        System.out.println(buf.readByte());
        System.out.println(buf.readByte());
//        log(buf);

        buf.markReaderIndex();
        System.out.println(buf.readInt());
        log(buf);
        buf.resetReaderIndex();
        System.out.println(buf.readInt());
        log(buf);
    }

    /**
     * 调试方法，打印ByteBuf中的具体情况
     * @param buffer
     */
    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
