package script.tasks.training.prayer;

import api.component.ExWilderness;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import script.wrappers.GEWrapper;
import script.wrappers.SupplyMapWrapper;
import script.wrappers.WalkingWrapper;

import java.lang.reflect.WildcardType;

public class LeaveWilderness extends Task {

    private static final Position LVL_30_POSITION = new Position(3069, 3746, 0);
    private static final Area WILDERNESS_AREA = Area.rectangular(2933, 3872, 3360, 3522);

    @Override
    public boolean validate() {
        return Skills.getLevel(Skill.PRAYER) == 50 && WILDERNESS_AREA.contains(Players.getLocal());
    }

    @Override
    public int execute() {
        boolean isHighLevelWilderness = ExWilderness.getLevel() > 30;

        if(WILDERNESS_AREA.contains(Players.getLocal())){
            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition());
        }

        return 1000;
    }
}
