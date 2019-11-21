package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.function.Predicate;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_0 extends Task {

    private boolean hasItems = false;
    private boolean hasGear = false;
    private boolean readyToStartQuest = false;

    private static final String ADAMANT_SCIMITAR = "Adamant scimitar";
    private static final String GLORY = "Amulet of glory(6)";
    private static final String RING_OF_RECOIL = "Ring of recoil";
    private static final String BUCKET = "Bucket";
    private static final String RUNE_ESSENCE = "Rune essence";
    private static final String VARROCK_TELEPORT = "Varrock teleport";
    private static final String Monkfish = "Monkfish";
    private static final String COINS = "Coins";
    private static final String STAMINA_POTION = "Stamina potion(4)";
    private static final Predicate<String> SURE = o -> o.contains("Sure.");

    private static final Position KING_ROALD_POSITION = new Position(3222, 3473, 0);

    private static final Area GE_AREA = Area.rectangular(3157, 3489, 3171, 3477);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10
                && PRIEST_IN_PERIL.getVarpValue() == 0
                && Skills.getLevel(Skill.PRAYER) < 50
                && PriestInPeril_Preparation.readyToStartPriestInPeril;
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

        if (!Movement.isStaminaEnhancementActive() && Movement.getRunEnergy() < Random.mid(5, 20)) {
            if (Inventory.contains(x -> x.getName().contains(STAMINA_POTION))) {
                Item staminaPotion = Inventory.getFirst(x -> x.getName().contains(STAMINA_POTION));
                if (staminaPotion.interact("Drink")) {
                    Time.sleepUntil(() -> Movement.isStaminaEnhancementActive(), 5000);
                }
            }
        }

        Npc kingRoald = Npcs.getNearest("King Roald");
        if (kingRoald == null) {
            Movement.walkToRandomized(KING_ROALD_POSITION);
        }
        if (kingRoald != null) {
            if (kingRoald.isPositionInteractable()) {
                if (!Dialog.isOpen()) {
                    kingRoald.interact("Talk-to");
                }
                if (Dialog.isOpen()) {
                    if (Dialog.canContinue()) {
                        Dialog.processContinue();
                    }
                    Dialog.process("Sure.");
                }
            }
        }


        return lowRandom();
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}


