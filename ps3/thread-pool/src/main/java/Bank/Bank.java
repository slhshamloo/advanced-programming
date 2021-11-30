package Bank;

import Tasks.Task;
import Thread.BankRunnable;
import Thread.BankThreadPool;

import java.util.ArrayList;

public class Bank {
    private final BankThreadPool threadPool;

    public Bank(int atmCount) {
        threadPool = new BankThreadPool(atmCount);
    }

    public ArrayList<Object> runATM(ArrayList<Task> tasks, Handler handler) {
        ArrayList<Object> results = new ArrayList<>();
        BankRunnable runnable = new BankRunnable(tasks, results, handler);

        threadPool.execute(runnable);
        return results;
    }

}
