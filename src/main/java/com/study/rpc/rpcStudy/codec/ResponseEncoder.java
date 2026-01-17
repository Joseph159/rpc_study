package com.study.rpc.rpcStudy.codec;

import com.alibaba.fastjson.JSONObject;
import com.study.rpc.rpcStudy.message.Message;
import com.study.rpc.rpcStudy.message.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder extends MessageToByteEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        // 需要内容：数据长度，魔数，消息类型，消息内容
        byte[] logic = Message.LOGIC;
        byte messageType = Message.MessageType.RESPONSE.getCode();
        byte[] body = serializeResponse(response);
        int length = logic.length + Byte.BYTES + body.length;

        out.writeInt(length);
        out.writeBytes(logic);
        out.writeByte(messageType);
        out.writeBytes(body);
    }

    // 序列化消息
    private byte[] serializeResponse(Response response) {
        return JSONObject.toJSONString(response).getBytes(StandardCharsets.UTF_8);
    }
}
