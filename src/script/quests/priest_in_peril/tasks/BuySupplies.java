package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.SleepWrapper;

public class BuySupplies extends Task {
    @Override
    public boolean validate() {
        return !Movement.isRunEnabled();
    }

    @Override
    public int execute() {
        return SleepWrapper.shortSleep350();
    }
}
