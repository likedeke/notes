package com.like.netty.protocol.custom.message.protocol;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.like.netty.protocol.custom.handler.LikeChannelMustPipeline;
import com.like.netty.protocol.custom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Create By like On 2021-04-11 14:41
 * 必须 配合该解码器使用 {@link LikeChannelMustPipeline#getLikeProtocolFrameDecoder()}
 */
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    private final ObjectMapper mapper = new ObjectMapper();
    public static final byte[] magicNumber = "LikeLove".getBytes();
    public int version = 1;
    public int serializationType = 0;

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        final ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(magicNumber);
        out.writeInt(version);
        out.writeByte(serializationType);
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        JSONUtil.toJsonStr(msg);

        byte[] msgArray = JSONSerialization(msg); ;

        out.writeInt(msgArray.length);
        // 不可变 共22 个字节
        // 7.写入消息
        out.writeBytes(msgArray);

        outList.add(out);
    }

    /**
     * json serialization
     *
     * @param msg message
     * @return {@link byte[]}
     */
    private byte[] JSONSerialization(Message msg) throws JsonProcessingException {
        return mapper.writeValueAsString(msg).getBytes();
        // return JSONUtil.toJsonStr(msg).getBytes();
    }

    /**
     * json deserialization
     *
     * @param msg message
     * @return {@link Message}
     */
    private Message JSONDeserialization(byte[] msg) throws JsonProcessingException {
        // return mapper.readValue(StrUtil.str(msg, Charset.defaultCharset()), Message.class);
        String json = StrUtil.str(msg, Charset.defaultCharset());
        return (Message) JSONUtil.toBean(json, Message.getMessageClass(JSONUtil.parse(json).getByPath("messageType", Integer.class)));
    }

    /**
     * jdk serialization
     *
     * @param msg message
     * @return {@link byte[]}
     * @throws IOException ioexception
     */
    private byte[] JDKSerialization(Message msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        return baos.toByteArray();
    }

    /**
     * jdk deserialization
     *
     * @param msg message
     * @return {@link Message}
     * @throws IOException            ioexception
     * @throws ClassNotFoundException 类没有发现异常
     */
    private Message JDKDeserialization(byte[] msg) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(msg));
        return (Message) ois.readObject();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String magicNumber = in.readBytes(8).toString(Charset.defaultCharset());
        if (!"LikeLove".equals(magicNumber)) return;

        int version = in.readInt();
        byte serializationType = in.readByte();
        byte messageType = in.readByte();
        int seqId = in.readInt();
        int msgArrayLen = in.readInt();
        byte[] msg = new byte[msgArrayLen];
        in.readBytes(msg, 0, msgArrayLen);


        Message message = JSONDeserialization(msg);
      /*  log.info("#decode(..):magicNumber:{}", magicNumber);
        log.info("#decode(..):version:{}", version);
        log.info("#decode(..):serializationType:{}", serializationType);
        log.info("#decode(..):messageType:{}", messageType);
        log.info("#decode(..):seqId:{}", seqId);
        log.info("#decode(..):msgArrayLen:{}", msgArrayLen);
        log.info("#decode(..):message:{}", message);*/

        out.add(message);
    }

    private final static Logger log = getLogger(MessageCodecSharable.class);
}
