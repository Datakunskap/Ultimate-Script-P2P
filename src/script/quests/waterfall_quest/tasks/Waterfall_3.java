package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.waterfall_quest.data.Quest;
import script.wrappers.MovementBreaks;

public class Waterfall_3 extends Task {

    private static final int KEY = 293;
    private static final int CRATE_FOR_KEY = 1990;
    private static final int LADDER = 5250;
    private static final int GOLRIE_DOOR = 1991;
    private static final int TOMB = 1992;

    private static final String TUNA = "Tuna";
    private static final String GAMES_NECKLACE = "Games necklace(";
    private static final String STAMINA_POTION = "Stamina potion(";
    private static final String GLARIALS_PEBBLE = "Glarial's pebble";
    private static final String CLIMB_DOWN_ACTION = "Climb-down";
    private static final String SEARCH_ACTION = "Search";
    private static final String GOLRIE = "Golrie";
    private static final String OPEN_ACTION = "Open";

    private static final Position LADDER_POSITION = new Position(2534, 3155);
    private static final Position CRATE_FOR_KEY_POSITION = new Position(2548, 9566);
    private static final Position GOLRIE_DOOR_POSITION = new Position(2515, 9575);
    private static final Position GOLRIE_POSITION = new Position(2515, 9580);
    private static final Position TOMB_POSITION = new Position(2555, 3444);

    private static final Area DUNGEON_AREA = Area.rectangular(2505, 9585, 2558, 9547);

    @Override
    public boolean validate() {
        return Quest.WITCHES_HOUSE.getVarpValue() == 7 && Quest.WATERFALL.getVarpValue() == 3;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_3");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.doEating(18);

        if (!API.inventoryHasItem(false, GLARIALS_PEBBLE, 1)) {
            if (!API.inventoryHasItem(false, KEY, 1) && !GOLRIE_POSITION.isPositionInteractable()) {
                if (!API.playerIsAt(DUNGEON_AREA)) {
                    API.interactWithSceneobject(LADDER, CLIMB_DOWN_ACTION, LADDER_POSITION);
                }
                if (API.playerIsAt(DUNGEON_AREA)) {
                    API.interactWithSceneobject(CRATE_FOR_KEY, SEARCH_ACTION, CRATE_FOR_KEY_POSITION);
                }
            }
            if (API.inventoryHasItem(false, KEY, 1) || GOLRIE_POSITION.isPositionInteractable()) {
                if (API.playerIsAt(DUNGEON_AREA)) {
                    if (!API.inventoryHasItem(false, GLARIALS_PEBBLE, 1)) {
                        if (!GOLRIE_POSITION.isPositionInteractable()) {
                            if (GOLRIE_DOOR_POSITION.distance() > 2) {
                                Movement.walkTo(GOLRIE_DOOR_POSITION, MovementBreaks::shouldBreakOnTarget);
                            }
                            if (GOLRIE_DOOR_POSITION.distance() <= 2) {
                                Log.info("1");
                                API.interactWithSceneobjectWithoutMoving(GOLRIE_DOOR, OPEN_ACTION);
                            }
                        }
                        if (GOLRIE_POSITION.isPositionInteractable()) {
                            Log.info("2");
                            API.talkTo(GOLRIE, GOLRIE_POSITION);
                        }
                    }
                }
            }
        }
        if (API.inventoryHasItem(false, GLARIALS_PEBBLE, 1)) {
            if (GOLRIE_POSITION.distance() < 10) {
                Movement.walkTo(BankLocation.BARBARIAN_ASSAULT.getPosition(), MovementBreaks::shouldBreakOnTarget);
            }
            if (BankLocation.BARBARIAN_ASSAULT.getPosition().distance() <= 50) {
                if (Inventory.containsAnyExcept(x -> x.getName().contains(GLARIALS_PEBBLE)
                        || x.getName().contains(TUNA)
                        || x.getName().contains(GAMES_NECKLACE)
                        || x.getName().contains(STAMINA_POTION))
                        || Equipment.getOccupiedSlots().length > 0) {
                    Log.info("I have something in my inventory or equipment I don't need");
                    if (!Bank.isOpen()) {
                        Log.info("Opening bank");
                        Bank.open();
                    }
                    if (Bank.isOpen()) {
                        Log.info("Depositing inventory");
                        if (Equipment.getOccupiedSlots().length > 0) {
                            Bank.depositEquipment();
                            Time.sleepUntilForDuration(() -> Equipment.getOccupiedSlots().length == 0, Random.nextInt(600, 800), 5000);
                        }
                        if (!Inventory.isEmpty()) {
                            Bank.depositAllExcept(GLARIALS_PEBBLE);
                            Time.sleepUntilForDuration(Inventory::isEmpty, Random.nextInt(600, 800), 5000);
                        }
                        if (!Inventory.contains(x -> x.getName().contains(GLARIALS_PEBBLE)
                                && x.getName().contains(TUNA)
                                && x.getName().contains(GAMES_NECKLACE)
                                && x.getName().contains(STAMINA_POTION))) {
                            API.withdrawItem(false,GLARIALS_PEBBLE, 1);
                            API.withdrawItem(false,GAMES_NECKLACE, 1);
                            API.withdrawItem(false,STAMINA_POTION, 4);
                            API.withdrawItem(false,TUNA, 10);
                        }
                    }
                }
            }
        }
        if (Inventory.containsOnly(x -> x.getName().contains(GLARIALS_PEBBLE)
                || x.getName().contains(TUNA)
                || x.getName().contains(GAMES_NECKLACE)
                || x.getName().contains(STAMINA_POTION))
                && Equipment.getOccupiedSlots().length == 0) {
            API.useItemOn(GLARIALS_PEBBLE, TOMB, TOMB_POSITION);
        }

        return API.lowRandom();
    }

}