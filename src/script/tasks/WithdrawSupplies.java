package script.tasks;

import org.rspeer.script.task.Task;

public class WithdrawSupplies extends Task {

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {
        return 1000;
    }
}
