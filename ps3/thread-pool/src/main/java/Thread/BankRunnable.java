package Thread;

import Bank.ATM;
import Bank.Handler;
import Results.Result;
import Tasks.Task;

import java.util.List;

public class BankRunnable implements Runnable {
    private final List<Task> tasks;
    private final List<Object> results;
    private final Handler handler;

    public BankRunnable(List<Task> tasks, List<Object> results, Handler handler) {
        this.tasks = tasks;
        this.results = results;
        this.handler = handler;
    }

    public void setATM(ATM atm) {
        tasks.forEach(task -> task.setATM(atm));
    }

    @Override
    public void run() {
        for (Task task : tasks) {
            try {
                Result result = task.run();
                synchronized (results) {
                    results.add(result);
                }
            } catch (Exception exception) {
                synchronized (results) {
                    results.add(exception);
                }
            }
        }
        handler.done();
    }
}
