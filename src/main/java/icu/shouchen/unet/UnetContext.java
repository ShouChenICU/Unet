package icu.shouchen.unet;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;

/**
 * Unet context
 *
 * @author shouchen
 * @date 2023/6/15
 */
public class UnetContext {
    private static final ThreadLocal<DatagramChannel> CHANNEL_THREAD_LOCAL;
    private static final ThreadLocal<UnetPipeline> PIPELINE_THREAD_LOCAL;
    private static final ThreadLocal<Iterator<DataHandler>> DATA_HANDLER_ITERATOR_THREAD_LOCAL;
    public static final ThreadLocal<SocketAddress> SOCKET_ADDRESS_THREAD_LOCAL;

    static {
        CHANNEL_THREAD_LOCAL = new ThreadLocal<>();
        PIPELINE_THREAD_LOCAL = new ThreadLocal<>();
        DATA_HANDLER_ITERATOR_THREAD_LOCAL = new ThreadLocal<>();
        SOCKET_ADDRESS_THREAD_LOCAL = new ThreadLocal<>();
    }

    public static DatagramChannel channel() {
        return CHANNEL_THREAD_LOCAL.get();
    }

    public static void channel(DatagramChannel channel) {
        CHANNEL_THREAD_LOCAL.set(channel);
    }

    public static UnetPipeline pipeline() {
        return PIPELINE_THREAD_LOCAL.get();
    }

    public static void pipeline(UnetPipeline pipeline) {
        PIPELINE_THREAD_LOCAL.set(pipeline);
    }

    public static SocketAddress address() {
        return SOCKET_ADDRESS_THREAD_LOCAL.get();
    }

    public static void address(SocketAddress address) {
        SOCKET_ADDRESS_THREAD_LOCAL.set(address);
    }

    static void dataHandlerIterator(Iterator<DataHandler> iterator) {
        DATA_HANDLER_ITERATOR_THREAD_LOCAL.set(iterator);
    }

    public static void doRead(ByteBuffer buffer) throws Exception {
        Iterator<DataHandler> iterator = DATA_HANDLER_ITERATOR_THREAD_LOCAL.get();
        if (iterator != null && iterator.hasNext()) {
            DataHandler dataHandler = iterator.next();
            dataHandler.handle(buffer);
        }
    }

    public static void doSend(ByteBuffer buffer) throws Exception {
        Iterator<DataHandler> iterator = DATA_HANDLER_ITERATOR_THREAD_LOCAL.get();
        if (iterator != null && iterator.hasNext()) {
            DataHandler dataHandler = iterator.next();
            dataHandler.handle(buffer);
        } else {
            channel().send(buffer, SOCKET_ADDRESS_THREAD_LOCAL.get());
        }
    }

    public static void clear() {
        CHANNEL_THREAD_LOCAL.remove();
        PIPELINE_THREAD_LOCAL.remove();
        DATA_HANDLER_ITERATOR_THREAD_LOCAL.remove();
        SOCKET_ADDRESS_THREAD_LOCAL.remove();
    }
}
