package icu.shouchen.unet;

/**
 * Logger
 *
 * @author shouchen
 * @date 2023/6/16
 */
public abstract class UnetLogger {
    private static UnetLogger logger;

    static {
        logger = new UnetLogger() {
            @Override
            public UnetLogger debug0(Object msg) {
                if (msg instanceof Exception) {
                    ((Exception) msg).printStackTrace();
                } else {
                    System.out.println(msg);
                }
                return this;
            }

            @Override
            public UnetLogger info0(Object msg) {
                if (msg instanceof Exception) {
                    ((Exception) msg).printStackTrace();
                } else {
                    System.out.println(msg);
                }
                return this;
            }

            @Override
            public UnetLogger warn0(Object msg) {
                if (msg instanceof Exception) {
                    ((Exception) msg).printStackTrace();
                } else {
                    System.out.println(msg);
                }
                return this;
            }

            @Override
            public UnetLogger error0(Object msg) {
                if (msg instanceof Exception) {
                    ((Exception) msg).printStackTrace();
                } else {
                    System.err.println(msg);
                }
                return this;
            }
        };
    }

    public static UnetLogger debug(Object msg) {
        return logger.debug0(msg);
    }

    public static UnetLogger info(Object msg) {
        return logger.info0(msg);
    }

    public static UnetLogger warn(Object msg) {
        return logger.warn0(msg);
    }

    public static UnetLogger error(Object msg) {
        return logger.error0(msg);
    }

    public static void logger(UnetLogger logger) {
        UnetLogger.logger = logger;
    }

    public abstract UnetLogger debug0(Object msg);

    public abstract UnetLogger info0(Object msg);

    public abstract UnetLogger warn0(Object msg);

    public abstract UnetLogger error0(Object msg);
}
