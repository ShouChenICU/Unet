package icu.shouchen.unet;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * ByteBuffer pool
 *
 * @author shouchen
 * @date 2023/6/18
 */
public class BufferPool {
    private final List<ByteBuffer> buffers;
    private final boolean isDirect;
    private final int capacity;
    private final int coreSize;
    private final int maxSize;
    private int currentSize;

    public BufferPool(int capacity, boolean isDirect, int coreSize, int maxSize) {
        buffers = new LinkedList<>();
        this.capacity = capacity;
        this.isDirect = isDirect;
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        currentSize = 0;
    }

    public ByteBuffer buffer() throws InterruptedException {
        synchronized (buffers) {
            if (buffers.size() > 0) {
                return buffers.remove(0).clear();
            } else if (currentSize < maxSize) {
                currentSize++;
                if (isDirect) {
                    return ByteBuffer.allocateDirect(capacity);
                }
                return ByteBuffer.allocate(capacity);
            } else {
                UnetLogger.warn("Buffer pool is full, max size is " + maxSize);
                buffers.wait();
                return buffer();
            }
        }
    }

    public void release(ByteBuffer buffer) {
        synchronized (buffers) {
            if (buffers.size() < coreSize) {
                buffers.add(buffer);
                buffers.notify();
            } else {
                currentSize--;
            }
        }
    }
}
