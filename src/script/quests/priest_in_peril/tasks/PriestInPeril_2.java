package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import java.util.function.Predicate;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_2 extends Task {

    private static final String STAMINA_POTION = "Stamina potion(";
    private static final String TUNA = "Tuna";
    private static final Predicate<String> YES = o -> o.contains("Yes");

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 2;
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

        Npc templeGuardian = Npcs.getNearest("Temple guardian");
        if (templeGuardian == null) {
            SceneObject trapDoorClosed = SceneObjects.getNearest(1579);
            SceneObject trapDoorOpen = SceneObjects.getNearest(1581);
            if (trapDoorClosed != null) {
                if (trapDoorClosed.containsAction("Open")) {
                    if (trapDoorClosed.interact("Open")) {
                        Time.sleepUntil(() -> trapDoorOpen.containsAction("Climb-down"), 10000);
                    }
                }

            }
            if (trapDoorOpen.containsAction("Climb-down")) {
                if (!Dialog.isOpen()) {
                    if (trapDoorOpen.interact("Climb-down")) {
                        Time.sleepUntil(() -> Dialog.isOpen(), 10000);
                    }
                }
                if (Dialog.isOpen()) {
                    if (Dialog.canContinue()) {
                        Dialog.processContinue();
                    }
                    if (Dialog.getChatOption(YES).isVisible()) {
                        Dialog.process(YES);
                    }
                }
            }
        }
        if (templeGuardian != null) {
            if (!local.isMoving() && !local.isAnimating() && !local.isHealthBarVisible()) {
                templeGuardian.interact("Attack");
            }
            if (Health.getPercent() < 40) {
                int amountOfFoodBeforeEating = Inventory.getCount(TUNA);
                Item tuna = Inventory.getFirst(TUNA);
                if (tuna.interact("Eat")) {
                    Time.sleepUntil(() -> Inventory.getCount(TUNA) == amountOfFoodBeforeEating - 1, 2000);
                }
            }
        }


        return lowRandom();
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}
