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
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.function.Predicate;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_5 extends Task {

    private boolean checked3493 = false;
    private boolean checked3494 = false;
    private boolean checked3495 = false;
    private boolean checked3496 = false;
    private boolean checked3497 = false;
    private boolean checked3498 = false;
    private boolean checked3499 = false;

    private static final String STAMINA_POTION = "Stamina potion(";
    private static final Predicate<String> YES = o -> o.contains("Yes");

    private static final Position WELL_POSITION = new Position(3422, 9890, 0);

    private static final Area INSIDE_TEMPLE = Area.rectangular(3409, 3494, 3418, 3482);
    private static final Area DOWNSTAIRS = Area.rectangular(3399, 9911, 3446, 9876);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 5;
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

        if (Inventory.contains("Golden key")) {
            if (!DOWNSTAIRS.contains(local)) {
                if (local.getFloorLevel() == 2) {
                    SceneObject ladder = SceneObjects.getNearest(16679);
                    if (ladder != null) {
                        if (ladder.interact("Climb-down")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 1, 1000);
                        }
                    }
                }
                if (local.getFloorLevel() == 1) {
                    SceneObject staircase = SceneObjects.getNearest(16673);
                    if (staircase != null) {
                        if (staircase.interact("Climb-down")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 1, 1000);
                        }
                    }
                }
                if (local.getFloorLevel() == 0) {
                    SceneObject trapDoorClosed = SceneObjects.getNearest(1579);
                    SceneObject trapDoorOpen = SceneObjects.getNearest(1581);
                    if (INSIDE_TEMPLE.contains(local)) {
                        SceneObject door = SceneObjects.getNearest("Large door");
                        if (door != null) {
                            if (door.interact("Open")) {
                                Time.sleepUntil(() -> !INSIDE_TEMPLE.contains(local), 10000);
                            }
                        }
                    }
                    if (!INSIDE_TEMPLE.contains(local)) {
                        if (trapDoorClosed != null) {
                            if (trapDoorClosed.containsAction("Open")) {
                                if (trapDoorClosed.interact("Open")) {
                                    Time.sleepUntil(() -> trapDoorOpen.containsAction("Climb-down"), 10000);
                                }
                            }
                        }
                        if (trapDoorOpen != null) {
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
                    }
                }
            }
            if (DOWNSTAIRS.contains(local)) {
                if (WELL_POSITION.distance() > 15) {
                    Movement.walkToRandomized(WELL_POSITION);
                }
                if (WELL_POSITION.distance() < 15) {
                    if (!checked3493) {
                        SceneObject monument3 = SceneObjects.getNearest(3493);
                        if (!Interfaces.isOpen(272)) {
                            if (monument3.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Log.info("Setting 3493 to true");
                                Time.sleep(2000);
                                checked3493 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3493)) ;
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }

                        }

                    }
                    if (checked3493 && !checked3494) {
                        SceneObject monument4 = SceneObjects.getNearest(3494);
                        if (!Interfaces.isOpen(272)) {
                            if (monument4.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Time.sleep(2000);
                                checked3494 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3494)) ;
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }
                        }

                    }
                    if (checked3494 && !checked3495) {
                        SceneObject monument5 = SceneObjects.getNearest(3495);
                        if (!Interfaces.isOpen(272)) {
                            if (monument5.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Time.sleep(2000);
                                checked3495 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3495)) ;
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }
                        }

                    }
                    if (checked3495 && !checked3496) {
                        SceneObject monument6 = SceneObjects.getNearest(3496);
                        if (!Interfaces.isOpen(272)) {
                            if (monument6.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Time.sleep(2000);
                                checked3496 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3496)) ;
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }
                        }

                    }
                    if (checked3496 && !checked3497) {
                        SceneObject monument7 = SceneObjects.getNearest(3497);
                        if (!Interfaces.isOpen(272)) {
                            if (monument7.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Time.sleep(2000);
                                checked3497 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3497)) ;
                                    Log.info("Used key on monument");
                                    Time.sleep(2000);
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }
                        }

                    }
                    if (checked3497 && !checked3498) {
                        SceneObject monument8 = SceneObjects.getNearest(3498);
                        if (!Interfaces.isOpen(272)) {
                            if (monument8.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Time.sleep(2000);
                                checked3498 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3498)) ;
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }
                        }

                    }
                    if (checked3498 && !checked3499) {
                        SceneObject monument9 = SceneObjects.getNearest(3499);
                        if (!Interfaces.isOpen(272)) {
                            if (monument9.interact("Study")) {
                                Time.sleepUntil(() -> Interfaces.isOpen(272), 10000);
                            }
                        }
                        if (Interfaces.isOpen(272)) {
                            if (Interfaces.getComponent(272, 8).getItemId() != 2945) {
                                Interfaces.getComponent(272, 1, 11).click();
                                Time.sleep(2000);
                                checked3499 = true;
                            }
                            if (Interfaces.getComponent(272, 8).getItemId() == 2945) {
                                if (Interfaces.getComponent(272, 1, 11).click()) {
                                    Time.sleepUntil(() -> Interfaces.getComponent(272, 1) == null, 5000);
                                    Time.sleep(2000);
                                    Predicate<Item> goldenKey = i -> i.getName().equals("Golden key");
                                    if (useItemOnObject("Golden key", 3499)) ;
                                    Time.sleepUntil(() -> Inventory.contains("Iron key"), 10000);
                                }
                            }
                        }


                    }
                }
            }
        }

        if (Inventory.contains("Iron key")) {
            if (DOWNSTAIRS.contains(local)) {
                if (Inventory.contains("Bucket")) {
                    Predicate<Item> bucket = i -> i.getName().equals("Bucket");
                    SceneObject well = SceneObjects.getNearest(3485);
                    if (Inventory.use(bucket, well)) {
                        Time.sleepUntil(() -> Inventory.contains("Murky water"), 10000);
                    }
                }
                if (Inventory.contains("Murky water")) {
                    SceneObject ladder = SceneObjects.getNearest(17385);
                    if (ladder != null) {
                        if (!ladder.isPositionWalkable()) {
                            Movement.walkToRandomized(ladder);
                        }
                        if (ladder.isPositionWalkable()) {
                            if (ladder.interact("Climb-up")) {
                                Time.sleepUntil(() -> !DOWNSTAIRS.contains(local), 20000);
                            }
                        }
                    }
                }
            }
            if (local.getFloorLevel() == 0) {
                SceneObject staircase = SceneObjects.getNearest(16671);
                if (staircase != null) {
                    if (!staircase.isPositionInteractable()) {
                        Movement.walkToRandomized(staircase);
                    }
                    if (staircase.isPositionInteractable()) {
                        if (staircase.interact("Climb-up")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 1, 1000);
                        }
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
                Predicate<Item> ironKey = i -> i.getName().equals("Iron key");
                SceneObject jailDoor = SceneObjects.getNearest(3463);
                if (!Dialog.isOpen()) {
                    if (jailDoor != null) {
                        Npc drezel = Npcs.getNearest(3488);
                        if (!drezel.isPositionWalkable()) {
                            if (Inventory.use(ironKey, jailDoor)) {
                                Time.sleepUntil(() -> Dialog.isOpen(), 5000);
                            }
                        }
                        if (drezel.isPositionWalkable()) {
                            if (drezel != null) {
                                if (!Dialog.isOpen()) {
                                    if (drezel.interact("Talk-to")) {
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
            }
        }

        return lowRandom();

    }

    public static boolean useItemOnObject(String itemName, String objectName) {
        return useItemOnObject(itemName, SceneObjects.getNearest(objectName).getId());
    }

    public static boolean useItemOnObject(String itemName, Position objectPosition) {
        return useItemOnObject(itemName, SceneObjects.getFirstAt(objectPosition).getId());
    }

    public static boolean useItemOnObject(String itemName, int objectID) {
        Item item = Inventory.getFirst(a -> a.getName().equalsIgnoreCase(itemName));
        if (item != null && (item.interact("Use") || item.interact(ActionOpcodes.ITEM_ACTION_0))) {
            Time.sleepUntil(Inventory::isItemSelected, 5000);
            Time.sleep(300, 600);
            SceneObject object = SceneObjects.getNearest(objectID);
            if (object != null && (object.interact(ActionOpcodes.ITEM_ON_OBJECT) || object.click())) {
                Time.sleepUntil(() -> Players.getLocal().isAnimating() && !Inventory.isItemSelected(), 5000);
                Time.sleepUntil(() -> !Players.getLocal().isAnimating() && !Players.getLocal().isMoving(), 5000);
                return true;
            }
        }
        return false;
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}
