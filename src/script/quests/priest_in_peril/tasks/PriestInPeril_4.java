package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_4 extends Task {

    private boolean talkedToDrezel = false;

    private static final String STAMINA_POTION = "Stamina potion(";
    private static final String RUNE_ESSENCE = "Rune essence";
    private static final String VARROCK_TELEPORT = "Varrock teleport";
    private static final String EMPTY_BUCKET = "Bucket";
    private static final String MONKFISH = "Tuna";

    private static final Position TEMPLE_DOOR = new Position(3407, 3488, 0);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 4;
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

        if (!Inventory.contains("Golden key")) {
            if (!Inventory.contains(RUNE_ESSENCE)) {
                if (!Bank.isOpen()) {
                    if (Bank.open()) {
                        Time.sleepUntil(() -> Bank.isOpen(), 20000);
                    }
                }
                if (Bank.isOpen()) {
                    if (Bank.depositInventory()) {
                        Time.sleepUntil(() -> Inventory.isEmpty(), 5000);
                    }
                    if (Inventory.isEmpty()) {
                        if (Bank.withdraw(VARROCK_TELEPORT, 2)) {
                            Time.sleepUntil(() -> Inventory.contains(VARROCK_TELEPORT), 5000);
                        }
                        if (Bank.withdraw(EMPTY_BUCKET, 1)) {
                            Time.sleepUntil(() -> Inventory.contains(EMPTY_BUCKET), 5000);
                        }
                        if (Bank.withdraw(MONKFISH, 2)) {
                            Time.sleepUntil(() -> Inventory.contains(MONKFISH), 5000);
                        }
                        if (Bank.withdraw(RUNE_ESSENCE, 24)) {
                            Time.sleepUntil(() -> Inventory.contains(RUNE_ESSENCE), 5000);
                        }
                    }
                }
            }
            if (Pickables.getNearest("Golden key") == null) {
                if (Inventory.contains(RUNE_ESSENCE)) {
                    if (TEMPLE_DOOR.distance() > 10) {
                        Movement.walkToRandomized(TEMPLE_DOOR);
                    }
                    if (TEMPLE_DOOR.distance() <= 10) {
                        Npc monk = Npcs.getNearest(3486);
                        SceneObject door = SceneObjects.getNearest("Large door");
                        if (!monk.isPositionInteractable()) {
                            if (door.interact("Open")) {
                                Time.sleepUntil(() -> monk.isPositionInteractable(), 5000);
                            }
                        }
                        if (monk.isPositionInteractable()) {
                            if (monk != null) {
                                if (!local.isMoving() && !local.isAnimating() && !local.isHealthBarVisible()) {
                                    monk.interact("Attack");
                                }
                                if (Health.getPercent() < 40) {
                                    int amountOfFoodBeforeEating = Inventory.getCount(MONKFISH);
                                    Item tuna = Inventory.getFirst(MONKFISH);
                                    if (tuna.interact("Eat")) {
                                        Time.sleepUntil(() -> Inventory.getCount(MONKFISH) == amountOfFoodBeforeEating - 1, 2000);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (Pickables.getNearest("Golden key") != null) {
                if (Inventory.isFull()) {
                    Item tuna = Inventory.getFirst(MONKFISH);
                    if (tuna.interact("Eat")) {
                        Time.sleepUntil(() -> !Inventory.isFull(), 5000);
                    }
                }
                if (!Inventory.isFull()) {
                    if (Pickables.getNearest("Golden key").interact("Take")) {
                        Time.sleepUntil(() -> Inventory.contains("Golden key"), 5000);
                    }
                }
            }
        }

        if (Inventory.contains("Golden key")) {
            if (!talkedToDrezel) {
                if (local.getFloorLevel() == 0) {
                    SceneObject staircase = SceneObjects.getNearest(16671);
                    if (staircase != null) {
                        if (staircase.interact("Climb-up")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 1, 1000);
                        }
                    }
                }
                if (local.getFloorLevel() == 1) {
                    SceneObject ladder = SceneObjects.getNearest(16683);
                    if (ladder != null) {
                        if (ladder.interact("Climb-up")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 2, 1000);
                        }
                    }
                }
                if (local.getFloorLevel() == 2) {
                    SceneObject jailDoor = SceneObjects.getNearest(3463);
                    if (!Dialog.isOpen()) {
                        if (jailDoor != null) {
                            if (jailDoor.interact("Talk-through")){
                                Time.sleepUntil(()-> Dialog.isOpen(), 5000);
                            }
                        }
                    }
                    if(Dialog.isOpen()){
                        if(Dialog.canContinue()){
                            Dialog.processContinue();
                        }
                        if(Dialog.isViewingChatOptions()){
                            Dialog.process(0);
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
