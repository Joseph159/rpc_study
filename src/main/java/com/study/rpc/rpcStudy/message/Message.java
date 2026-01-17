package com.study.rpc.rpcStudy.message;

import lombok.Data;

import java.nio.charset.StandardCharsets;


@Data
public class Message {
    public static final byte[] LOGIC = "XIAOHUOLONG".getBytes(StandardCharsets.UTF_8);

    private byte[] logic;

    private byte messageType;

    private byte[] body;

    public enum MessageType {
        REQUEST(1),
        RESPONSE(2);

        private final byte code;

        MessageType(int code) {
            this.code = (byte) code;
        }

        public byte getcode() {
            return code;
        }

    }
}
