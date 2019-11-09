package script.quests.nature_spirit.wrappers;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import script.data.Locations;
import script.quests.nature_spirit.data.Location;
import script.tasks.fungus.Fungus;

public class WalkingWrapper {

    public static void walkToNatureGrotto() {
        if (Location.NATURE_GROTTO_BRIDGE_POSITION.distance() > 3) {
            Log.fine("Walking To Nature Grotto");
            Movement.walkTo(Location.NATURE_GROTTO_BRIDGE_POSITION,
                    () -> {
                        if (script.wrappers.WalkingWrapper.shouldBreakOnTarget() || (inMortania() && inSalveGravyardArea())) {
                            if (inMortania() && inSalveGravyardArea()) {
                                Log.fine("Handling Gate");
                                handleGate();
                            } else {
                                Movement.toggleRun(true);
                                if (Players.getLocal().getHealthPercent() < 20) {
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

    private static void handleGate() {
        Player local = Players.getLocal();
        SceneObject gate = SceneObjects.getNearest("Gate");
        InterfaceComponent enterTheSwamp = Interfaces.getComponent(580, 17);
        InterfaceComponent dontAskMeThisAgain = Interfaces.getComponent(580, 20);
        Log.info("Opening the gate");
        if (gate != null) {
            if (gate.containsAction("Open")) {
                if (gate.interact("Open")) {
                    Time.sleepUntil(() -> !Fungus.AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local) || enterTheSwamp != null, 30_000);
                    if (dontAskMeThisAgain != null && dontAskMeThisAgain.getMaterialId() == 941) {
                        Log.info("dontAskMeThisAgain is visible");
                        if (dontAskMeThisAgain.interact("Off/On")) {
                            Log.info("Clicked enterTheSwamp");
                            Time.sleepUntil(() -> dontAskMeThisAgain.getMaterialId() == 942, 5000);
                        }
                    }
                    if (enterTheSwamp != null) {
                        if (enterTheSwamp.getMaterialId() == 942) {
                            Log.info("enterTheSwamp is visible and dontAskMeAgain is toggled");
                            if (enterTheSwamp.interact("Yes")) {
                                Log.info("Clicked enterTheSwamp");
                                Time.sleepUntil(() -> !Fungus.AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local), 5000);
                            }
                        }
                    }
                    if (dontAskMeThisAgain == null && enterTheSwamp != null) {
                        Log.info("enterTheSwamp is visible, but dontAskMeAgain isn't");
                        if (enterTheSwamp.interact("Yes")) {
                            Log.info("Clicked enterTheSwamp");
                            Time.sleepUntil(() -> !Fungus.AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local), 5000);
                        }
                    }
                }
            }
        }
    }
}
