package Tasks;

import Bank.ATM;
import Exceptions.NoCardInsertedException;
import Results.Result;

import java.util.ArrayList;

public interface Task {
    public Result run() throws Exception;
    public void setATM(ATM atm);
}
