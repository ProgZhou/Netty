package com.java.netty.demo01.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.java.netty.demo01.util.ByteBufferUtil.debugAll;

/** ByteBuffer测试
 * @author ProgZhou
 * @createTime 2022/05/10
 */
@Slf4j
public class NettyTest1 {
    /*解决黏包，半包问题
    * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
    但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为

    * Hello,world\n
    * I'm zhangsan\n
    * How are you?\n

    变成了下面的两个 byteBuffer (黏包，半包)

    Hello,world\nI'm zhangsan\nHo
    w are you?\n
    *
    * */
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,World\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    //实现split方法分离接收的信息
    private static void split(ByteBuffer source) {
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

    @Test
    public void test() {
        //FileChannel
        //1. 可以使用输入输出流
        //2. 也可以使用RandomAccessFile

        FileChannel channel = null;
        try {
            //获取channel
            channel = new FileInputStream("D:/Java_idea/JavaNetty/demo01/src/data.txt").getChannel();
            //准备缓冲区，allocate()方法分配缓冲区的大小
            ByteBuffer buffer = ByteBuffer.allocate(10);
            //从channel中读取数据，并写入buffer
            while (true) {
                int read = channel.read(buffer);
                if(read == -1) {
                    break;
                }
                //打印buffer的内容
                buffer.flip();  //切换buffer为读模式
                //检查buffer中是否含有剩余的数据
                while (buffer.hasRemaining()) {
                    //无参的get默认一个字节一个字节读取
                    byte b = buffer.get();
                    log.debug("{}", (char) b);
                }
                buffer.clear(); //每次读取完成之后将buffer切换为写模式
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test1() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        //debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        //debugAll(buffer);
        buffer.flip();
        debugAll(buffer);
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.rewind();
        debugAll(buffer);
    }

    @Test
    public void test2() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        buffer.flip();

        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        buffer.mark();
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        debugAll(buffer);
        buffer.reset();
        debugAll(buffer);
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
    }

    //ByteBuffer与String之间的相互转换
    @Test
    public void test3() {
        //1. 使用buffer的put方法
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);

        //2. 使用Charset中的encode方法
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        //3. 使用ByteBuffer自带的wrap方法
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        //切换回String，调用Charset的decode方法
        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);
    }

    //Scattering Reads: 适用于事先知道文件中待读取的各个部分的长度，分散读
    @Test
    public void test4() {
        try (FileChannel channel = new RandomAccessFile("D:\\Java_idea\\JavaNetty\\demo01\\src\\words.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[] {b1,b2,b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (IOException e) {
        }
    }

    //Gathering Writes：组合写
    @Test
    public void test5() {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
        try (FileChannel channel = new RandomAccessFile("D:\\Java_idea\\JavaNetty\\demo01\\src\\words2.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[] {b1,b2,b3});
        } catch (IOException e) {
        }

    }
}
