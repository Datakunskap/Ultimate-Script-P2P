package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_1 extends Task {

    private static final String STAMINA_POTION = "Stamina potion(";

    private static final Position TEMPLE_DOOR = new Position(3407, 3488, 0);

    private static final Area GE_AREA = Area.rectangular(3157, 3489, 3171, 3477);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 1;
    }

    @Override
    public int execute() {
        Player local = Players.getLocal();

        if (Dialog.canContinue()) {
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Movement.toggleRun(true);
            }
        }

        if (!Movement.isStaminaEnhancementActive()) {
            if (Inventory.contains(x -> x.getName().contains(STAMINA_POTION))) {
                Item staminaPotion = Inventory.getFirst(x -> x.getName().contains(STAMINA_POTION));
                if (staminaPotion.interact("Drink")) {
                    Time.sleepUntil(() -> Movement.isStaminaEnhancementActive(), 5000);
                }
            }
        }

        if (TEMPLE_DOOR.distance() > 10) {
            Movement.walkToRandomized(TEMPLE_DOOR);
        }

        if (TEMPLE_DOOR.distance() <= 10) {
            if (!Dialog.isOpen()) {
                SceneObject door = SceneObjects.getNearest("Large door");
                door.interact("Knock-at");
            }
            if (Dialog.isOpen()) {
                if (Dialog.canContinue()) {
                    Dialog.processContinue();
                }
                if (Dialog.isViewingChatOptions()) {
                    Dialog.process(0);
                }
            }
        }

        return lowRandom();
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}
