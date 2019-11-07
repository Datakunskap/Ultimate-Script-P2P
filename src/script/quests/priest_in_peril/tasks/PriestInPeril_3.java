package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_3 extends Task {

    private boolean talkedToDoor = false;

    private static final String STAMINA_POTION = "Stamina potion(";

    private static final Position KING_ROALD_POSITION = new Position(3222, 3473, 0);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 3;
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

        if (!talkedToDoor) {
            if (Interfaces.getComponent(229, 1) != null) {
                if (Interfaces.getComponent(229, 1).getText().contains("HAHAHAHA")) {
                    Log.info("Setting talkedToDoor to true");
                    talkedToDoor = true;
                }
            }
        }

        if (!talkedToDoor) {
            SceneObject door = SceneObjects.getNearest("Large door");
            if (door == null) {
                SceneObject ladder = SceneObjects.getNearest("Ladder");
                if (ladder != null) {
                    if (ladder.interact("Climb-up")) {
                        Time.sleepUntil(() -> door != null, 5000);
                    }
                }
            }
            if (door != null) {
                if (!Dialog.isOpen()) {
                    if (door.interact("Knock-at")) {
                        Time.sleepUntil(() -> Dialog.isOpen(), 10000);
                    }
                }
                if (Dialog.isOpen()) {
                    if (Dialog.canContinue()) {
                        Dialog.processContinue();
                    }
                }
            }
        }

        if (talkedToDoor) {
            if (KING_ROALD_POSITION.distance() > 10) {
                Movement.walkToRandomized(KING_ROALD_POSITION);
            }
            if (KING_ROALD_POSITION.distance() <= 10) {
                Npc kingRoald = Npcs.getNearest("King Roald");
                if (kingRoald != null) {
                    if (kingRoald.getPosition().isPositionInteractable()) {
                        if (!Dialog.isOpen()) {
                            if (kingRoald.interact("Talk-to")) {
                                Time.sleepUntil(() -> Dialog.isOpen(), 5000);
                            }
                        }
                        if (Dialog.isOpen()) {
                            if (Dialog.canContinue()) {
                                Dialog.processContinue();
                            }
                        }
                    }
                }
            }
        }

        return lowRandom();
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}
