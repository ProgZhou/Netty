package com.java.netty.demo01.nettybasic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/** FileChannel测试
 * @author ProgZhou
 * @createTime 2022/05/19
 */
@Slf4j
public class NettyTest2 {
    //文件内容传输
    @Test
    public void test1() {
        try (
                FileChannel from = new FileInputStream("D:\\Java_idea\\JavaNetty\\demo01\\src\\from.txt").getChannel();
                FileChannel to = new FileOutputStream("D:\\Java_idea\\JavaNetty\\demo01\\src\\to.txt").getChannel();
                ) {
            /* transferTo()的参数
            *  long position: 待穿数据在源文件的起始位置
            *  long count: 待传输数据的大小
            *  WritableByteChannel: 传输数据的目的地
            * */
            from.transferTo(0, from.size(), to);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //大文件传输
    @Test
    public void test2() {
        try (
                FileChannel from = new FileInputStream("D:\\Java_idea\\JavaNetty\\demo01\\src\\from.txt").getChannel();
                FileChannel to = new FileOutputStream("D:\\Java_idea\\JavaNetty\\demo01\\src\\to.txt").getChannel();
                ) {
            long size = from.size();
            long left = size;
            while(left > 0) {
                //transferTo方法有一个返回值，返回实际传输数据的大小
                log.debug("position: {}, left: {}", size - left, left);
                left -= from.transferTo((size - left), left, to);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //常用API测试
    @Test
    public void test3() throws IOException {
        //遍历某个文件夹，并统计这个文件夹下文件的个数和目录的个数
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\jdk1.8.0_74"), new SimpleFileVisitor<Path>() {
            //访问目录
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("dir: {}", dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            //访问文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("{}",file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        log.debug("fileCount: {}", fileCount);
        log.debug("dirCount: {}", dirCount);
    }

    //API测试
    @Test
    public void test4() throws IOException {
        //统计jar包的数量
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("C:\\Program Files\\Java\\jdk1.8.0_74"), new SimpleFileVisitor<Path>() {
            //访问文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    log.debug("{}",file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        log.debug("jar: {}", jarCount);
    }
}
