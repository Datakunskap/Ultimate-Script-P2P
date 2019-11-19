package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.MovementBreaks;

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class Waterfall_4 extends Task {

    private static final int CLOSED_CHEST = 1994;
    private static final int OPEN_CHEST = 1995;
    private static final int TOMB = 1993;
    private static final int ROCK = 1996;
    private static final int LEDGE = 2010;
    private static final int TREE = 2020;

    private static final String OPEN_ACTION = "Open";
    private static final String SEARCH_ACTION = "Search";
    private static final String GLARIALS_AMULET = "Glarial's amulet";
    private static final String GLARIALS_URN = "Glarial's urn";
    private static final String WATER_RUNE = "Water rune";
    private static final String EARTH_RUNE = "Earth rune";
    private static final String AIR_RUNE = "Air rune";
    private static final String TUNA = "Tuna";
    private static final String ROPE = "Rope";
    private static final String STAMINA_POTION = "Stamina potion(";
    private static final String LUMBRIDGE_TELEPORT = "Lumbridge teleport";

    private static final Position CHEST_POSITION = new Position(2531, 9844);
    private static final Position TOMB_POSITION = new Position(2542, 9810);
    private static final Position LEDGE_POSITION = new Position(2511, 3463);
    private static final Position ROCK_POSITION = new Position(2512, 3476);
    private static final Position TREE_POSITION = new Position(2512, 3466);

    private static final Area DUNGEON_AREA = Area.rectangular(2523, 9850, 2559, 9808);
    private static final Area CAVE_AREA = Area.rectangular(2555, 9919, 2597, 9860);
    private static final Area ISLAND_ONE_AREA = Area.rectangular(2510, 3482, 2513, 3475);
    private static final Area ISLAND_TWO_AREA = Area.rectangular(2511, 3470, 2514, 3465);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 4;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_4");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.doEating(20);

        if (!API.inventoryHasItem(false, GLARIALS_AMULET, 1)
                || !API.inventoryHasItem(false, GLARIALS_URN, 1)) {
            if (API.playerIsAt(DUNGEON_AREA)) {
                if (!API.inventoryHasItem(false, GLARIALS_AMULET, 1)) {
                    if (CHEST_POSITION.distance() > 1) {
                        Log.info("Setting flag");
                        Movement.setWalkFlag(CHEST_POSITION);
                        Time.sleepUntil(() -> CHEST_POSITION.distance() <= 1, API.lowRandom());
                    }
                    if (CHEST_POSITION.distance() <= 1) {
                        API.interactWithSceneobjectWithoutMoving(CLOSED_CHEST, OPEN_ACTION);
                        API.interactWithSceneobjectWithoutMoving(OPEN_CHEST, SEARCH_ACTION);
                    }
                }
                if (API.inventoryHasItem(false, GLARIALS_AMULET, 1)) {
                    if (!API.inventoryHasItem(false, GLARIALS_URN, 1)) {
                        if (TOMB_POSITION.distance() > 1) {
                            Log.info("Setting flag");
                            Movement.setWalkFlag(TOMB_POSITION);
                            Time.sleepUntil(() -> TOMB_POSITION.distance() <= 1, API.lowRandom());
                        }
                        if (TOMB_POSITION.distance() <= 1) {
                            API.interactWithSceneobjectWithoutMoving(TOMB, SEARCH_ACTION);
                        }
                    }
                }
            }
        }

        if (API.inventoryHasItem(false, GLARIALS_AMULET, 1)
                && API.inventoryHasItem(false, GLARIALS_URN, 1)
                || API.inventoryHasItem(false, GLARIALS_URN, 1)
                && API.isWearingItem(GLARIALS_AMULET)) {
            if (!API.playerIsAt(CAVE_AREA)) {
                if (!Inventory.containsAll(GLARIALS_URN, WATER_RUNE, AIR_RUNE, EARTH_RUNE, TUNA, ROPE, LUMBRIDGE_TELEPORT) || !Equipment.contains(GLARIALS_AMULET)) {
                    if (BankLocation.BARBARIAN_ASSAULT.getPosition().distance() > 50) {
                        Movement.walkTo(BankLocation.BARBARIAN_ASSAULT.getPosition(), MovementBreaks::shouldBreakOnTarget);
                    }
                    if (BankLocation.BARBARIAN_ASSAULT.getPosition().distance() <= 50) {
                        API.wearItem(GLARIALS_AMULET);
                        if (!Bank.isOpen()) {
                            Bank.open();
                        }
                        if (Bank.isOpen()) {
                            if (Inventory.containsAnyExcept(GLARIALS_URN, WATER_RUNE, AIR_RUNE, EARTH_RUNE, TUNA, ROPE, STAMINA_POTION, LUMBRIDGE_TELEPORT)) {
                                Bank.depositInventory();
                                Time.sleepUntil(() -> Inventory.isEmpty(), 5000);
                            }
                            if (!Inventory.containsAll(GLARIALS_URN, WATER_RUNE, AIR_RUNE, EARTH_RUNE, TUNA, ROPE, STAMINA_POTION, LUMBRIDGE_TELEPORT)) {
                                API.withdrawItem(false, GLARIALS_URN, 1);
                                API.withdrawItem(true, WATER_RUNE, 6);
                                API.withdrawItem(true, AIR_RUNE, 6);
                                API.withdrawItem(true, EARTH_RUNE, 6);
                                API.withdrawItem(false, TUNA, 15);
                                API.withdrawItem(false, ROPE, 1);
                                API.withdrawItem(false, STAMINA_POTION, 2);
                                API.withdrawItem(false, LUMBRIDGE_TELEPORT, 1);
                            }
                        }
                    }
                }
                if (Inventory.containsAll(GLARIALS_URN, WATER_RUNE, AIR_RUNE, EARTH_RUNE, TUNA, ROPE, LUMBRIDGE_TELEPORT) && Equipment.contains(GLARIALS_AMULET)) {
                    if (!API.playerIsAt(ISLAND_ONE_AREA) && !API.playerIsAt(ISLAND_TWO_AREA) && LEDGE_POSITION.distance() > 0) {
                        Movement.walkTo(ISLAND_ONE_AREA.getCenter(), MovementBreaks::shouldBreakOnTarget);
                    }
                    if (API.playerIsAt(ISLAND_ONE_AREA) && !API.playerIsAt(ISLAND_TWO_AREA) && LEDGE_POSITION.distance() > 0) {
                        API.useItemOn(ROPE, ROCK, ROCK_POSITION);
                        Time.sleep(API.highRandom());
                    }
                    if (!API.playerIsAt(ISLAND_ONE_AREA) && API.playerIsAt(ISLAND_TWO_AREA) && LEDGE_POSITION.distance() > 0) {
                        API.useItemOn(ROPE, TREE, TREE_POSITION);
                        Time.sleep(API.highRandom());
                    }
                    if (!API.playerIsAt(ISLAND_ONE_AREA) && !API.playerIsAt(ISLAND_TWO_AREA) && LEDGE_POSITION.distance() < 1) {
                        API.interactWithSceneobjectWithoutMoving(LEDGE, OPEN_ACTION);
                    }
                }
            }
        }

        return API.lowRandom();
    }

}