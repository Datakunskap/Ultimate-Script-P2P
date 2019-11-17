package script.tasks.training.prayer;

import api.component.ExWilderness;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;
import script.wrappers.SupplyMapWrapper;
import script.wrappers.WalkingWrapper;

import java.util.function.Predicate;

public class LeaveWilderness extends Task {

    private static final Position LVL_30_POSITION = new Position(3069, 3746, 0);
    private static final Area WILDERNESS_AREA = Area.rectangular(2933, 3872, 3360, 3522);
    private static final Predicate<Item> GLORY = x -> x.getName().contains("Amulet of glory(");

    @Override
    public boolean validate() {
        return Skills.getLevel(Skill.PRAYER) == 50 && ExWilderness.getLevel() > 30 && WILDERNESS_AREA.contains(Players.getLocal());
    }

    @Override
    public int execute() {
        boolean isHighLevelWilderness = ExWilderness.getLevel() > 30;

        if (isHighLevelWilderness) {
            if (Movement.getDaxWalker().isUseTeleports()) {
                Movement.getDaxWalker().setUseTeleports(false);
            }

            WalkingWrapper.walkToPosition(LVL_30_POSITION);

        } else {
            if (!Movement.getDaxWalker().isUseTeleports()) {
                Movement.getDaxWalker().setUseTeleports(true);
                if (Inventory.getFirst(GLORY).interact("Rub")) {
                    Time.sleepUntil(Dialog::isViewingChatOptions, SleepWrapper.longSleep7500());
                }
                if (Dialog.isViewingChatOptions()) {
                    if (Dialog.process(o -> o.toLowerCase().contains("edgevil"))) {
                        Time.sleepUntil(() -> !WILDERNESS_AREA.contains(Players.getLocal()), SleepWrapper.longSleep7500());
                    }
                }
            }
            GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
            if (WILDERNESS_AREA.contains(Players.getLocal())) {
                Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition());
            }
        }

        return SleepWrapper.mediumSleep1000();
    }
}
