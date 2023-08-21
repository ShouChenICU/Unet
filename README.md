# Unet

Unet is a lightweight and high performance UDP net framework developed using pure Java,
it based on event model drive and used synchronous non-blocking IO.

## Usage

```java
UnetConfig unetConfig = new UnetConfig()
        .bindPort(1234)
        .configurePipeline(pipeline -> pipeline
                .addInboundHandler(buffer -> {
                    // Do something...
                    // Pass to the next processor
                    UnetContext.doRead(buffer);
                }).addInboundHandler(buffer -> {
                    // Do something...
                }).addOutboundHandler(buffer -> {
                    // Outbound handler chain
                    // Do something
                    UnetContext.doSend(buffer);
                })
        );
Unet unet = Unet.spawn(unetConfig);

// Async send messages
unet.fireSend(
        ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8)),
        new InetSocketAddress("target address", 1234)
);

// Sync send messages
unet.fireSend(
        ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8)),
        new InetSocketAddress("target address", 1234)
).sync();
// sync call is synchronized, is equivalent to this
unet.pipeline().doSend(
        ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8)),
        new InetSocketAddress("target address", 1234)
);

// Broadcast
unet.fireBroadcast(
        ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8)),
        1234
);
```

## Build

**Requirement**

- JDK11+
- maven3

**Command**

```shell
mvn clean package
```

Now you can see it in the target folder.