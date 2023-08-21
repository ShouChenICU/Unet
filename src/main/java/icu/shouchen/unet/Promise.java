package icu.shouchen.unet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Promise
 *
 * @author shouchen
 * @date 2023/6/15
 */
public class Promise<T> implements RunnableFuture<T> {
    private final Supplier<T> supplier;
    private final AtomicBoolean isRan;
    private final AtomicBoolean isDone;
    private final AtomicReference<T> resultReference;
    private final AtomicReference<Exception> exceptionReference;
    private final CountDownLatch latch;

    public Promise(Supplier<T> supplier) {
        this.supplier = supplier;
        isRan = new AtomicBoolean(false);
        isDone = new AtomicBoolean(false);
        resultReference = new AtomicReference<>();
        exceptionReference = new AtomicReference<>();
        latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        if (isRan.getAndSet(true)) {
            return;
        }
        try {
            resultReference.set(supplier.get());
        } catch (Exception exception) {
            exceptionReference.set(exception);
        } finally {
            isDone.set(true);
            latch.countDown();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone.get();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        latch.await();
        if (exceptionReference.get() != null) {
            throw new ExecutionException(exceptionReference.get());
        }
        return resultReference.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        if (latch.await(timeout, unit)) {
            if (exceptionReference.get() != null) {
                throw new ExecutionException(exceptionReference.get());
            }
            return resultReference.get();
        }
        throw new TimeoutException("Time out");
    }

    public Promise<T> setSuccess(T result) {
        resultReference.set(result);
        isDone.set(true);
        latch.countDown();
        return this;
    }

    public Promise<T> setFailure(Exception exception) {
        exceptionReference.set(exception);
        isDone.set(true);
        latch.countDown();
        return this;
    }

    public void sync() throws InterruptedException, ExecutionException {
        get();
    }
}
