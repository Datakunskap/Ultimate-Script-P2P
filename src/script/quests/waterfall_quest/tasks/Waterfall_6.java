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
    private static final int AIR_RUNES = 6;
    private static final int EARTH_RUNES = 6;
    private static final int WATER_RUNES = 6;
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

        if (API.playerIsAt(PILLAR_AREA)) {

            final SceneObject PILLAR_ONE = SceneObjects.getFirstAt(new Position(2562, 9910));
            final SceneObject PILLAR_TWO = SceneObjects.getFirstAt(new Position(2562, 9912));
            final SceneObject PILLAR_THREE = SceneObjects.getFirstAt(new Position(2562, 9914));
            final SceneObject PILLAR_FOUR = SceneObjects.getFirstAt(new Position(2569, 9914));
            final SceneObject PILLAR_FIVE = SceneObjects.getFirstAt(new Position(2569, 9912));
            final SceneObject PILLAR_SIX = SceneObjects.getFirstAt(new Position(2569, 9910));

            if (Inventory.contains(AIR_RUNE) || Inventory.contains(WATER_RUNE) || Inventory.contains(EARTH_RUNE)) {
                useAllRunesOnPillar(PILLAR_ONE, 0);
                useAllRunesOnPillar(PILLAR_TWO, 1);
                useAllRunesOnPillar(PILLAR_THREE, 2);
                useAllRunesOnPillar(PILLAR_FOUR, 3);
                useAllRunesOnPillar(PILLAR_FIVE, 4);
                useAllRunesOnPillar(PILLAR_SIX, 5);
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

    public void useOneRuneOnPillar(int runeName, int runeStack, SceneObject pillarForOne, int amountOfRunes) {
        if (Inventory.getFirst(runeName) != null) {
            if (Inventory.getFirst(runeName).getStackSize() == runeStack - amountOfRunes) {
                Inventory.getFirst(runeName).interact(USE_ACTION);
                API.mediumRandom();
                pillarForOne.interact(USE_ACTION);
                API.highRandom();
            }
        }
    }

    public void useAllRunesOnPillar(SceneObject pillarForAll, int amountOfRunesToSubtract) {
        useOneRuneOnPillar(AIR_RUNE, AIR_RUNES, pillarForAll, amountOfRunesToSubtract);
        Time.sleep(API.mediumRandom());
        useOneRuneOnPillar(WATER_RUNE, WATER_RUNES, pillarForAll, amountOfRunesToSubtract);
        Time.sleep(API.mediumRandom());
        useOneRuneOnPillar(EARTH_RUNE, EARTH_RUNES, pillarForAll, amountOfRunesToSubtract);
        Time.sleep(API.mediumRandom());
    }

}