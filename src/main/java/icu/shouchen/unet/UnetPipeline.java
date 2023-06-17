package icu.shouchen.unet;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pipeline
 *
 * @author shouchen
 * @date 2023/6/15
 */
public class UnetPipeline {
    private final DatagramChannel datagramChannel;
    private final List<DataHandler> inboundHandlers;
    private final List<DataHandler> outboundHandlers;
    private Consumer<Exception> exceptionConsumer;

    public UnetPipeline(DatagramChannel channel) {
        datagramChannel = channel;
        inboundHandlers = new LinkedList<>();
        outboundHandlers = new LinkedList<>();
    }

    public UnetPipeline addInboundHandler(DataHandler handler) {
        inboundHandlers.add(handler);
        return this;
    }

    public UnetPipeline addOutboundHandler(DataHandler handler) {
        outboundHandlers.add(handler);
        return this;
    }

    public UnetPipeline doRead(ByteBuffer buffer) {
        UnetContext.pipeline(this);
        UnetContext.channel(datagramChannel);
        UnetContext.dataHandlerIterator(inboundHandlers.iterator());
        try {
            UnetContext.doRead(buffer);
        } catch (Exception exception) {
            exceptionCaught(exception);
        } finally {
            UnetContext.clear();
        }
        return this;
    }

    public UnetPipeline doSend(ByteBuffer buffer, SocketAddress address) {
        UnetContext.pipeline(this);
        UnetContext.channel(datagramChannel);
        UnetContext.dataHandlerIterator(outboundHandlers.iterator());
        try {
            UnetContext.address(address);
            UnetContext.doSend(buffer);
        } catch (Exception exception) {
            exceptionCaught(exception);
        } finally {
            UnetContext.clear();
        }
        return this;
    }

    public UnetPipeline doBroadcast(ByteBuffer buffer, int port) {
        return doSend(buffer, new InetSocketAddress(UnetConstant.BROADCAST_ADDRESS, port));
    }

    public void exceptionCaught(Consumer<Exception> exceptionConsumer) {
        this.exceptionConsumer = exceptionConsumer;
    }

    void exceptionCaught(Exception exception) {
        if (exceptionConsumer == null) {
            UnetLogger.warn(exception);
        } else {
            exceptionConsumer.accept(exception);
        }
    }
}
