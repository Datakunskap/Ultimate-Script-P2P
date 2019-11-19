package quests.the_restless_ghost;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import api.API;
import script.quests.the_restless_ghost.data.Quest;
import script.wrappers.MovementBreaks;

public class RestlessGhost_2 extends Task {

    public static final String GHOSTSPEAK_AMULET = "Ghostspeak amulet";
    public static final Position GHOST_POSITION = new Position(3248, 3193);

    @Override
    public boolean validate() {
        return Quest.THE_RESTLESS_GHOST.getVarpValue() == 2;
    }

    @Override
    public int execute() {

        Log.info("TheRestlessGhost_2");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.wearItem(GHOSTSPEAK_AMULET);

        if(GHOST_POSITION.distance() > 10 || !GHOST_POSITION.isPositionInteractable()){
            Log.info("Walking to the ghost");
            Movement.walkTo(GHOST_POSITION, MovementBreaks::shouldBreakOnRunenergy);
        }

        if(GHOST_POSITION.distance() <= 10){

            Dialog.process("Yep, now tell me what");

            SceneObject coffin = SceneObjects.getNearest("Coffin");
            if(coffin.containsAction("Close")){
                Npc ghost = Npcs.getNearest("Restless ghost");
                if(ghost != null){
                    if(!Dialog.isOpen()){
                        Log.info("Talking to the ghost");
                        ghost.interact("Talk-to");
                        Time.sleepUntil(()-> Dialog.isOpen(), 5000);
                    }
                }
            }
            if(coffin.containsAction("Open")){
                Log.info("Opening the chest");
                coffin.interact("Open");
                Time.sleepUntil(()-> !coffin.containsAction("Open"), 5000);
            }
        }

        return API.lowRandom();
    }

}
