package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
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
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;


public class RestlessGhost_0 extends Task {

    private static final Predicate<String> IM_LOOKING_FOR_PREDICATE = o -> o.contains("I'm looking for a quest!");
    private static final Predicate<String> OKAY_LET_ME_PREDICATE = o -> o.contains("Ok, let me help then.");

    private final Player local = Players.getLocal();

    @Override
    public boolean validate() {
        return Skills.getLevel(Skill.MAGIC) >= 13
                && THE_RESTLESS_GHOST.getVarpValue() == 0
                && WITCHES_HOUSE.getVarpValue() == 0
                && Skills.getLevel(Skill.PRAYER) == 1;
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        if(Skills.getLevel(Skill.HITPOINTS) != Skills.getCurrentLevel(Skill.HITPOINTS)){
            Log.info("Eating food");
            Item food = Inventory.getFirst("Tuna");
            if(food.interact("Eat")){
                Time.sleep(350,600);
            }
        }

        if(local.getHealthPercent() < 100){
            Item food = Inventory.getFirst("Tuna");
            if(food.interact("Eat")){
                Time.sleep(350,600);
            }
        }

        if (Movement.getRunEnergy() > 20 && !Movement.isRunEnabled()) {
            Movement.toggleRun(true);
        }

        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                if (Dialog.processContinue()) {
                }
            }
            Dialog.process("I'm looking for a quest!");
            Dialog.process("Ok, let me help then.");

        } else {
            Npc Father_Aereck = Npcs.getNearest(921);
            if (Father_Aereck != null) {
                Movement.walkToRandomized(Father_Aereck);
                Father_Aereck.interact("Talk-to");
                Time.sleepUntil(() -> Dialog.isOpen(), 10000);
            } else {
                Movement.walkToRandomized(new Position(3243, 3208));
            }
        }
        return 300;
    }

}


