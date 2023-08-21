package icu.shouchen.unet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Unet core class
 *
 * @author shouchen
 * @date 2023/4/24
 */
public class Unet {
    private final Selector selector;
    private final DatagramChannel datagramChannel;
    private final SelectionKey selectionKey;
    private final UnetPipeline pipeline;
    private final BufferPool bufferPool;
    private ExecutorService eventLoopExecutor;

    public static Unet spawn(UnetConfig config) throws IOException {
        return new Unet(config);
    }

    private Unet(UnetConfig config) throws IOException {
        initEventLoopExecutor(config.eventLoopExecutorSize());
        bufferPool = new BufferPool(
                config.bufferSize(),
                true,
                config.eventLoopExecutorSize(),
                config.eventLoopExecutorSize()
        );
        selector = Selector.open();
        datagramChannel = DatagramChannel.open();
        datagramChannel.socket().setBroadcast(true);
        datagramChannel.configureBlocking(false);
        pipeline = config.pipeline().datagramChannel(datagramChannel);
        selectionKey = datagramChannel.register(selector, SelectionKey.OP_READ);
        datagramChannel.bind(new InetSocketAddress(config.bindPort()));
        eventLoopExecutor.execute(this::selectLoop);
    }

    /**
     * Initial event loop executor
     *
     * @param size Thread pool size
     */
    private synchronized void initEventLoopExecutor(int size) {
        if (size == 0) {
            size = UnetConstant.CORE_COUNT;
        }
        eventLoopExecutor = new ThreadPoolExecutor(
                size,
                size,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(UnetConstant.MAX_JOB_COUNT),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public DatagramChannel datagramChannel() {
        return datagramChannel;
    }

    /**
     * Get pipeline
     *
     * @return UnetPipeline
     */
    public UnetPipeline pipeline() {
        return pipeline;
    }

    public Promise<UnetPipeline> fireSend(ByteBuffer buffer, SocketAddress address) {
        Promise<UnetPipeline> promise = new Promise<>(() -> pipeline.doSend(buffer, address));
        eventLoopExecutor.execute(promise);
        return promise;
    }

    public Promise<UnetPipeline> fireBroadcast(ByteBuffer buffer, int port) {
        Promise<UnetPipeline> promise = new Promise<>(() -> pipeline.doBroadcast(buffer, port));
        eventLoopExecutor.execute(promise);
        return promise;
    }

    public Unet execute(Runnable runnable) {
        eventLoopExecutor.execute(runnable);
        return this;
    }

    public void close() {
        selectionKey.cancel();
        try {
            selector.close();
        } catch (IOException exception) {
            UnetLogger.warn(exception);
        }
        try {
            datagramChannel.close();
        } catch (IOException exception) {
            UnetLogger.warn(exception);
        }
        eventLoopExecutor.shutdownNow();
        try {
            if (!eventLoopExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                UnetLogger.warn("There are some unfinished task");
            }
        } catch (InterruptedException exception) {
            UnetLogger.warn(exception);
        }
    }

    private void selectLoop() {
        try {
            if (selector.select() > 0) {
                final ByteBuffer buffer = bufferPool.buffer();
                final SocketAddress address = datagramChannel.receive(buffer);
                eventLoopExecutor.execute(() -> {
                    try {
                        buffer.flip();
                        UnetContext.unet(this);
                        UnetContext.address(address);
                        pipeline.doRead(buffer);
                    } catch (Exception exception) {
                        pipeline.exceptionCaught(exception);
                    } finally {
                        bufferPool.release(buffer);
                    }
                });
            }
            selector.selectedKeys().clear();
            eventLoopExecutor.execute(this::selectLoop);
        } catch (Exception exception) {
            close();
            UnetLogger.error(exception);
        }
    }
}
