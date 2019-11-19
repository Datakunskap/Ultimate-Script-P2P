package quests.witches_house;

import api.API;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.nature_spirit.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_2 extends Task {

    private static final String LADDER_NAME = "Ladder";
    private static final String GATE = "Gate";

    private static final Position LADDER_POSITION = new Position(2907, 9876);
    private static final Position CHEESE_POSITION = new Position(2903, 3466);

    private static final Area HOUSE_INSIDE = Area.rectangular(2901, 3476, 2937, 3459, 0);
    private static final Area BASEMENT = Area.rectangular(2899, 9890, 2937, 9850);
    private static final Area LADDER_AREA = Area.rectangular(2901, 3476, 2907, 3475);
    private static final Area HOUSE_MAIN_PART = Area.rectangular(2901, 3474, 2907, 3468);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 2;
    }

    @Override
    public int execute() {

        Log.info("WitchesHouse_2");

        Player local = Players.getLocal();

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        if (BASEMENT.contains(local)) {
            if (!LADDER_POSITION.isPositionInteractable()) {
                SceneObjects.getNearest(GATE).interact("Open");
                Time.sleepUntil(() -> !SceneObjects.getNearest(GATE).containsAction("Open"), 3000);
            }
            if (LADDER_POSITION.isPositionInteractable()) {
                API.interactWithSceneobject(LADDER_NAME, "Climb-up", LADDER_POSITION);
            }
        }

        if (HOUSE_INSIDE.contains(local)) {
            if (!CHEESE_POSITION.isPositionInteractable()) {
                if (LADDER_AREA.contains(local)) {
                    SceneObject door = SceneObjects.getNearest(24686);
                    door.interact("Open");
                    Time.sleepUntil(() -> !door.containsAction("Open"), 3000);
                }
                if (HOUSE_MAIN_PART.contains(local)) {
                    SceneObject door = SceneObjects.getNearest(24686);
                    door.interact("Open");
                    Time.sleepUntil(() -> !door.containsAction("Open"), 3000);
                }
            }
            if(CHEESE_POSITION.isPositionInteractable()){
                if(CHEESE_POSITION.distance() > 1) {
                    Movement.walkTo(CHEESE_POSITION);
                }
                if(CHEESE_POSITION.distance() <= 1){
                    Npc mouse = Npcs.getNearest("Mouse");
                    if(mouse == null)
                        Inventory.getFirst("Cheese").interact("Drop");
                    Time.sleepUntil(()-> Npcs.getNearest("Mouse") != null, 5000);
                    if(mouse != null){
                        Inventory.use(x -> x.getName().equals("Magnet"), mouse);
                    }
                }
            }
        }


        return API.lowRandom();
    }

}