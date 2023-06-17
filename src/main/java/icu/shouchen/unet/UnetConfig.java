package icu.shouchen.unet;

/**
 * Unet config
 *
 * @author shouchen
 * @date 2023/6/17
 */
public class UnetConfig {
    private int eventLoopExecutorSize;
    private int bindPort;
    private int bufferSize;

    public UnetConfig() {
        eventLoopExecutorSize = UnetConstant.CORE_COUNT;
        bindPort = 0;
        bufferSize = UnetConstant.UDP_PACKET_LENGTH;
    }

    public int eventLoopExecutorSize() {
        return eventLoopExecutorSize;
    }

    public UnetConfig eventLoopExecutorSize(int eventLoopExecutorSize) {
        this.eventLoopExecutorSize = eventLoopExecutorSize;
        return this;
    }

    public int bindPort() {
        return bindPort;
    }

    public UnetConfig bindPort(int bindPort) {
        this.bindPort = bindPort;
        return this;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public UnetConfig bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }
}
