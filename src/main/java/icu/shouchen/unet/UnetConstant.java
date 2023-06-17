package icu.shouchen.unet;

/**
 * Some constants
 *
 * @author shouchen
 * @date 2023/4/24
 */
public interface UnetConstant {

    /**
     * Broadcast address
     */
    String BROADCAST_ADDRESS = "255.255.255.255";

    /**
     * UDP packet length limit
     */
    int UDP_PACKET_LENGTH = 548;

    /**
     * Core count
     */
    int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * Max job count
     */
    int MAX_JOB_COUNT = 4096;
}
