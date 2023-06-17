package icu.shouchen.unet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author shouchen
 * @date 2023/6/17
 */
public class UnetUtils {
    public static String buf2str(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
