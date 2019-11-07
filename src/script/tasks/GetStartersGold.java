package script.tasks;

import org.rspeer.script.task.Task;
import script.wrappers.SleepWrapper;

public class GetStartersGold extends Task {
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {
        return SleepWrapper.shortSleep350();
    }
}
