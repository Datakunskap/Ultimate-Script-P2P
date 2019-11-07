package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.function.Predicate;

import static script.quests.the_restless_ghost.data.Quest.THE_RESTLESS_GHOST;


public class RestlessGhost_1 extends Task {
    private static final Predicate<String> GOT_A_GHOST_PREDICATE = o -> o.contains("He's got a ghost");
    private static final Predicate<String> FATHER_AERECK_SENT_PREDICATE = o -> o.contains("Father Aereck sent me");

    public int randomSleep(){
        if (Movement.isRunEnabled()) {
            return Random.mid(2500,4000);
        } else {
            return Random.mid(4000,9000);
        }
    }

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 1;
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        if (Movement.getRunEnergy() > 20 && !Movement.isRunEnabled()) { //Turn on run if it's off with over 20 energy
            Movement.toggleRun(true);

        }

        if (Dialog.isOpen()){
            if (Dialog.canContinue()) {
                if (Dialog.processContinue()) {
                }
            }
            Dialog.process("Father Aereck sent me to talk to you.");
            Dialog.process("He's got a ghost haunting his graveyard.");
        } else {
            final Npc Father_U = Npcs.getNearest(923);
            if (Father_U != null) {
                Movement.walkTo(Father_U);
                Time.sleepUntil(() -> (local.isMoving()), 10000);
                Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
                Father_U.interact("Talk-to");
                Time.sleepUntil(() -> Dialog.isOpen(), 10000);
            } else {
                Movement.walkTo(new Position(3147, 3175, 0));
                Time.sleepUntil(() -> (local.isMoving()), 10000);
                Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
            }
        }
        return 300;
    }
}


