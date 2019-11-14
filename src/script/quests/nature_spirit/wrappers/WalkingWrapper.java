package script.quests.nature_spirit.wrappers;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import script.data.Locations;
import script.quests.nature_spirit.data.Location;
import script.tasks.fungus.Fungus;

public class WalkingWrapper {

    private static Area MORTANIA = Area.rectangular(3410, 3454, 3520, 3321);
    private static Area BEFORE_GATE_AREA = Area.rectangular(3441, 3458, 3444, 3460);

    public static void walkToNatureGrotto() {
        if (Location.NATURE_GROTTO_BRIDGE_POSITION.distance() > 3) {
            Log.fine("Walking To Nature Grotto");
            if (!MORTANIA.contains(Players.getLocal()) && !inSalveGravyardArea()) {

                Fungus.useSalveGraveyardTeleport();
            }

            Movement.walkTo(Location.NATURE_GROTTO_BRIDGE_POSITION,
                    () -> {
                        if (script.wrappers.WalkingWrapper.shouldBreakOnTarget() || inSalveGravyardArea()) {
                            if (inSalveGravyardArea()) {
                                Log.fine("Handling Gate");
                                Fungus.handleGate();
                                return true;
                            } else {
                                Movement.toggleRun(true);
                                if (Players.getLocal().getHealthPercent() < 35) {
                                    Log.info("Eating");
                                    Item food = Inventory.getFirst(f -> f.containsAction("Eat"));
                                    if (food != null) {
                                        food.interact("Eat");
                                    }
                                }
                            }
                        }
                        return false;
                    });
        } else {
            crossGrottoBridge(true);
        }
    }

    public static void exitAndLeaveGrotto() {
        if (Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {
            SceneObjects.getNearest(3525).interact("Exit");
            Time.sleepUntil(() -> Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
        }
        if (Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            crossGrottoBridge(false);
        }
    }

    public static void crossGrottoBridge(boolean toGrotto) {
        if (Players.getLocal().getHealthPercent() < 35) {
            Log.info("Eating");
            Item food = Inventory.getFirst(f -> f.containsAction("Eat"));
            if (food != null) {
                food.interact("Eat");
            }
        }
        SceneObject bridge = SceneObjects.getNearest("Bridge");
        if (bridge != null && !Players.getLocal().isMoving() && bridge.interact(a -> true)) {
            if (toGrotto) {
                if (!Time.sleepUntil(() -> Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000)) {
                    Movement.setWalkFlag(Location.NATURE_GROTTO_AREA.getCenter());
                }
            } else {
                Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
            }
        }
    }

    public static void enterGrotto() {
        if (Inventory.contains("Ghostspeak amulet")) {
            Inventory.getFirst("Ghostspeak amulet").interact(a -> true);
            Time.sleepUntil(() -> Equipment.contains("Ghostspeak amulet"), 5000);
        }

        SceneObject grotto = SceneObjects.getNearest("Grotto");

        if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
            Time.sleepUntil(() -> Dialog.isOpen() && !Players.getLocal().isMoving(), 5000);
        }
    }

    private static boolean inMortania() {
        return Fungus.BLOOM_TILE.distance() > 5 && Fungus.BLOOM_TILE.distance() < 100;
    }

    private static boolean inSalveGravyardArea() {
        Player local = Players.getLocal();
        return Fungus.AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local);
    }
}
