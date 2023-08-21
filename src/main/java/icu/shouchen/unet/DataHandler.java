package icu.shouchen.unet;

import java.nio.ByteBuffer;

/**
 * Data handler
 *
 * @author shouchen
 * @date 2023/4/28
 */
public interface DataHandler {
    void handle(ByteBuffer buffer) throws Exception;
}
