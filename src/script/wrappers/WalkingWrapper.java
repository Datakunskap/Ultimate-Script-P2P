package script.wrappers;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Location;
import script.tasks.fungus.Fungus;

public class WalkingWrapper {

    private static Area MORTANIA = Area.rectangular(3410, 3454, 3534, 3319);
    public static final Position GATE_POSITION = new Position(3443, 3459, 0);

    public static boolean walkToPosition(Position position) {
        if (!position.equals(GATE_POSITION) && MORTANIA.contains(position) && !MORTANIA.contains(Players.getLocal()) && !Fungus.inSalveGravyardArea() && GATE_POSITION.distance() > 6) {
            if (Inventory.contains("Salve graveyard teleport") && GATE_POSITION.distance() > 10 && !Location.DUNGEON_AREA.contains(Players.getLocal())) {
                Fungus.useTeleportTab("Salve graveyard teleport");
            }
            walkToPosition(GATE_POSITION);
        }

        return Movement.walkTo(position,
                () -> {
                    if (shouldBreakOnTarget() || shouldEnableRun() || (Fungus.inSalveGravyardArea() && (MORTANIA.contains(position)))) {
                        if ((Fungus.inSalveGravyardArea() && MORTANIA.contains(position))) {
                            Log.fine("Handling Gate");
                            Fungus.handleGate();
                        } else {
                            if (!Movement.isRunEnabled())
                                Movement.toggleRun(true);
                        }
                    }
                    if (Players.getLocal().getHealthPercent() < 35 || Health.getPercent() < 35) {
                        consumeFirstConsumable();
                    }
                    return false;
                }) || position.distance() < 4;
    }

    public static boolean shouldEnableRun() {
        return (Movement.getRunEnergy() > Random.nextInt(5, 15) && !Movement.isRunEnabled());
    }

    public static boolean shouldBreakOnTarget() {
        Npc attacker = Npcs.getNearest(a -> true);
        Player me = Players.getLocal();
        return Movement.getRunEnergy() > 0
                && !Movement.isRunEnabled()
                && me.isHealthBarVisible()
                && attacker != null
                && attacker.getTarget() != null
                && attacker.getTarget().equals(me);
    }

    public static boolean shouldBreakOnRunenergy() {
        return (Movement.getRunEnergy() > Random.nextInt(5, 15) && !Movement.isRunEnabled())
                /*|| Movement.isStaminaEnhancementActive()*/;
    }

    public static boolean consumeFirstConsumable() {
        if (Inventory.isFull() && Inventory.contains("Rotten food") && Players.getLocal().getHealthPercent() > 35) {
            Inventory.getFirst("Rotten food").interact("Drop");
            return true;
        }
        Item food = Inventory.getFirst(f -> f.containsAction("Eat") || f.containsAction("Drink"));
        if (food != null) {
            if (food.containsAction("Eat")) {
                Log.info("Eating: " + food.getName());
                food.interact("Eat");
                return true;
            }
            if (food.containsAction("Drink")) {
                Log.info("Drinking: " + food.getName());
                food.interact("Drink");
                return true;
            }
        }
        return false;
    }
}
