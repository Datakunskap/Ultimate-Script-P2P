package script.tasks;

import api.component.ExWorldHopper;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.BankWrapper;

public class MemberWorldChecker extends Task {
    @Override
    public boolean validate() {
        RSWorld world = Worlds.get(Worlds.getCurrent());
        if (world != null && !world.isMembers() && world.getId() > 0 && !BankWrapper.isMuleing()) {
            return true;
        }
        return false;
    }

    @Override
    public int execute() {
        Log.info("World Hopping to P2P");
        ExWorldHopper.randomInstaHopInPureP2p();
        return 6000;
    }
}
