package com.java.netty.demo01.nettyadvanced.net4.protocol;

import com.java.netty.demo01.nettyadvanced.net4.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/** 自定义消息编解码器
 * @author ProgZhou
 * @createTime 2023/04/17
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    //编码方法
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //1. 4byte魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 1byte协议版本
        out.writeByte(1);
        //3. 1byte的序列化方式 比如 0代表jdk序列化  1代表json序列化
        out.writeByte(0);
        //4. 1byte的指令类型
        out.writeByte(msg.getMessageType());
        //5. 4byte的指令序号
        out.writeInt(msg.getSequenceId());
        //填充字节使得协议头长度为2的整数倍
        out.writeByte(0xff);
        //6 获取消息内容的字节数组，由于Message类实现了Serializable接口，所以可以通过ObjectOutputStream来获取字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //7. 4byte的消息正文的长度
        out.writeInt(bytes.length);

        //8. 写入内容
        out.writeBytes(bytes);
    }

    //解码方法
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //1. 读取魔数  4byte
        int magicNum = in.readInt();
        //2. 读取协议版本  1byte
        byte version = in.readByte();
        //3. 读取序列化方式  1byte
        byte serializerType = in.readByte();
        //4. 读取指令类型  1byte
        byte messageType = in.readByte();
        //5. 读取指令序号  4byte
        int sequenceId = in.readInt();
        // 读取填充字符
        in.readByte();  //直接跳过即可
        //6. 读取内容长度  4byte
        int length = in.readInt();
        //7. 分配空间，读取消息正文
        byte[] content = new byte[length];
        in.readBytes(content, 0, length);
        log.debug("magicNumber: {}, version: {}, serializerType: {}, messageType: {}, sequenceId: {}, length: {}",
                magicNum, version, serializerType, messageType, sequenceId, length);
        //8. 将读取到的内容反序列化为对象
        if(serializerType == 0) {
            //如果是jdk序列化
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(content));
            Message msg = (Message) ois.readObject();
            log.debug("content: {}", msg);
            out.add(msg);
        }


    }
}
