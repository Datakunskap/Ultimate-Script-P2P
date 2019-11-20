package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class Waterfall_6 extends Task {

    private static final int AIR_RUNE = 556;
    private static final int EARTH_RUNE = 557;
    private static final int WATER_RUNE = 555;
    private static final int GLARIALS_AMULET = 295;
    private static final int GLARIALS_STATUE = 2006;

    private static final String USE_ACTION = "Use";

    private static final Position GLARIAL_STATUE_POSITION = new Position(2565, 9916, 0);

    private static final Area PILLAR_AREA = Area.rectangular(2561, 9917, 2570, 9909);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 6;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_6");

        API.doDialog();

        API.toggleRun();

        if (!API.playerIsAt(PILLAR_AREA)) {
            Movement.walkTo(PILLAR_AREA.getCenter());
        }

        SceneObject PILLAR_ONE = SceneObjects.getFirstAt(new Position(2562, 9910));
        SceneObject PILLAR_TWO = SceneObjects.getFirstAt(new Position(2562, 9912));
        SceneObject PILLAR_THREE = SceneObjects.getFirstAt(new Position(2562, 9914));
        SceneObject PILLAR_FOUR = SceneObjects.getFirstAt(new Position(2569, 9910));
        SceneObject PILLAR_FIVE = SceneObjects.getFirstAt(new Position(2569, 9912));
        SceneObject PILLAR_SIX = SceneObjects.getFirstAt(new Position(2569, 9914));

        if (API.playerIsAt(PILLAR_AREA)) {
            if (Inventory.contains(AIR_RUNE)) {
                if (Inventory.getCount(true, AIR_RUNE) == 6) {
                    API.useItemOn(AIR_RUNE, PILLAR_ONE);
                }
                if (Inventory.getCount(true, AIR_RUNE) == 5) {
                    API.useItemOn(AIR_RUNE, PILLAR_TWO);
                }
                if (Inventory.getCount(true, AIR_RUNE) == 4) {
                    API.useItemOn(AIR_RUNE, PILLAR_THREE);
                }
                if (Inventory.getCount(true, AIR_RUNE) == 3) {
                    API.useItemOn(AIR_RUNE, PILLAR_FOUR);
                }
                if (Inventory.getCount(true, AIR_RUNE) == 2) {
                    API.useItemOn(AIR_RUNE, PILLAR_FIVE);
                }
                if (Inventory.getCount(true, AIR_RUNE) == 1) {
                    API.useItemOn(AIR_RUNE, PILLAR_SIX);
                }
            }
            if (Inventory.contains(WATER_RUNE) && !Inventory.contains(AIR_RUNE)) {
                if (Inventory.getCount(true,WATER_RUNE) == 6) {
                    API.useItemOn(WATER_RUNE, PILLAR_ONE);
                }
                if (Inventory.getCount(true,WATER_RUNE) == 5) {
                    API.useItemOn(WATER_RUNE, PILLAR_TWO);
                }
                if (Inventory.getCount(true,WATER_RUNE) == 4) {
                    API.useItemOn(WATER_RUNE, PILLAR_THREE);
                }
                if (Inventory.getCount(true,WATER_RUNE) == 3) {
                    API.useItemOn(WATER_RUNE, PILLAR_FOUR);
                }
                if (Inventory.getCount(true,WATER_RUNE) == 2) {
                    API.useItemOn(WATER_RUNE, PILLAR_FIVE);
                }
                if (Inventory.getCount(true,WATER_RUNE) == 1) {
                    API.useItemOn(WATER_RUNE, PILLAR_SIX);
                }
            }
            if (Inventory.contains(EARTH_RUNE) && !Inventory.contains(AIR_RUNE) && !Inventory.contains(WATER_RUNE)) {
                if (Inventory.getCount(true,EARTH_RUNE) == 6) {
                    API.useItemOn(EARTH_RUNE, PILLAR_ONE);
                }
                if (Inventory.getCount(true,EARTH_RUNE) == 5) {
                    API.useItemOn(EARTH_RUNE, PILLAR_TWO);
                }
                if (Inventory.getCount(true,EARTH_RUNE) == 4) {
                    API.useItemOn(EARTH_RUNE, PILLAR_THREE);
                }
                if (Inventory.getCount(true,EARTH_RUNE) == 3) {
                    API.useItemOn(EARTH_RUNE, PILLAR_FOUR);
                }
                if (Inventory.getCount(true,EARTH_RUNE) == 2) {
                    API.useItemOn(EARTH_RUNE, PILLAR_FIVE);
                }
                if (Inventory.getCount(true,EARTH_RUNE) == 1) {
                    API.useItemOn(EARTH_RUNE, PILLAR_SIX);
                }
            }
            if (!Inventory.contains(AIR_RUNE, WATER_RUNE, EARTH_RUNE)) {
                if (Equipment.contains(GLARIALS_AMULET)) {
                    EquipmentSlot.NECK.unequip();
                }
                if (API.inventoryHasItem(false, GLARIALS_AMULET, 1)) {
                    API.useItemOn(GLARIALS_AMULET, GLARIALS_STATUE, GLARIAL_STATUE_POSITION);
                    Time.sleep(API.highRandom());
                }
            }
        }

        return API.lowRandom();
    }

}