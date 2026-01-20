package com.study.rpc.rpcStudy.codec;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.study.rpc.rpcStudy.message.Message;
import com.study.rpc.rpcStudy.message.Request;
import com.study.rpc.rpcStudy.message.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class XHLDefineDecoder extends LengthFieldBasedFrameDecoder {
    public XHLDefineDecoder() {
        super(1024 * 1024, 0, Integer.BYTES, 0, Integer.BYTES);
    }
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);

        byte[] logic = new byte[Message.LOGIC.length];

        // 校验魔数
        frame.readBytes(logic);
        if (!Arrays.equals(logic, Message.LOGIC)) {
            throw new IllegalArgumentException("魔数有误");
        }

        byte messageType = frame.readByte();
        byte[] body = new byte[frame.readableBytes()];
        frame.readBytes(body);
        if (Objects.equals(messageType, Message.MessageType.REQUEST.getCode())) {
            return deserializeRequest(body);
        }
        if (Objects.equals(messageType, Message.MessageType.RESPONSE.getCode())) {
            return deserializeResponse(body);
        }
        throw new IllegalArgumentException("消息类型不支持");
    }

    private Request deserializeRequest(byte[] body) {
        return JSONObject.parseObject(new String(body, StandardCharsets.UTF_8), Request.class, JSONReader.Feature.SupportClassForName);
    }

    private Response deserializeResponse(byte[] body) {
        return JSONObject.parseObject(new String(body, StandardCharsets.UTF_8), Response.class);
    }
}
