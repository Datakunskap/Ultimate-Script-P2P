package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.script.task.Task;

public class RestlessGhost0 extends Task {
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {
        return Random.mid(300,450);
    }
}
