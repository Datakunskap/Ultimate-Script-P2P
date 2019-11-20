package script.quests.witches_house.tasks;

import api.API;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_4 extends Task {

    public static final String BALL = "Ball";
    public static final String FALADOR_TELEPORT = "Falador teleport";
    public static final String BOY = "Boy";

    public static final Position BOY_POSITION = new Position(2929, 3456);

    public static final Area SHED = Area.rectangular(2934, 3467, 2937, 3459);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 6;
    }

    @Override
    public int execute() {

        Log.info("WitchesHouse_4");

        Player local = Players.getLocal();

        API.doDialog();

        API.toggleRun();

        if (!Inventory.contains(BALL)) {
            Pickable ball = Pickables.getNearest(BALL);
            if (ball.interact("Take")) {
                Time.sleepUntil(() -> Inventory.contains(BALL), 10_000);
            }
        }

        if (Inventory.contains(BALL)) {
            if (SHED.contains(local)) {
                Item faladorTeleport = Inventory.getFirst(FALADOR_TELEPORT);
                if (faladorTeleport != null) {
                    faladorTeleport.interact("Break");
                    Time.sleepUntil(() -> !SHED.contains(local), 5000);
                }
                if (faladorTeleport == null) {
                    if (Magic.canCast(Spell.Modern.HOME_TELEPORT)) {
                        Magic.cast(Spell.Modern.HOME_TELEPORT);
                        Time.sleepUntil(() -> !SHED.contains(local), 30000);
                    }
                }
            }
            if (!SHED.contains(local)) {
                API.talkTo(BOY, BOY_POSITION);
            }
        }
        return API.lowRandom();
    }

}