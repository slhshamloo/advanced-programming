package Thread;

import Bank.ATM;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class BankThreadPool {
    private final Queue<BankRunnable> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isExecuting = new AtomicBoolean(true);

    public BankThreadPool(int threadCount) {
        IntStream.range(0, threadCount).forEach(i -> new Thread(new BankThreadRunnable(queue, isExecuting)).start());
    }

    public void execute(BankRunnable runnable) {
        if (!isExecuting.get())
            throw new IllegalStateException("Thread pool isn't running");
        synchronized (queue) {
            queue.add(runnable);
            queue.notify();
        }
    }

    public void execute(Collection<BankRunnable> runnables) {
        runnables.forEach(this::execute);
    }

    public void stop() {
        isExecuting.set(false);
    }

    public void terminate() {
        queue.clear();
        stop();
    }

    private static class BankThreadRunnable implements Runnable {
        private final Queue<BankRunnable> queue;
        private final AtomicBoolean isExecuting;

        private final ATM atm = new ATM();

        public BankThreadRunnable(Queue<BankRunnable> runnables, AtomicBoolean isExecuting) {
            this.queue = runnables;
            this.isExecuting = isExecuting;
        }

        @Override
        public void run() {
            BankRunnable runnable;
            while (isExecuting.get()) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                runnable = queue.poll();
                runnable.setATM(atm);
                runnable.run();
            }
        }
    }
}
