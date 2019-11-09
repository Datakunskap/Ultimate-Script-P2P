package script.wrappers;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Location;
import script.tasks.fungus.Fungus;

public class WalkingWrapper {

    public static void walkToPosition(Position position) {
        Movement.walkTo(position,
                () -> {
                    if (shouldBreakOnTarget() || shouldBreakOnRunenergy() || (Fungus.inMortania() && Fungus.inSalveGravyardArea())) {
                        if (Fungus.inMortania() && Fungus.inSalveGravyardArea()) {
                            Log.fine("Handling Gate");
                            handleGate();
                        } else {
                            Movement.toggleRun(true);
                            if (Players.getLocal().getHealthPercent() < 20) {
                                Item food = Inventory.getFirst(f -> f.containsAction("Eat"));
                                if (food != null) {
                                    Log.fine("Eating");
                                    food.interact("Eat");
                                }
                            }
                        }
                    }
                    return false;
                });
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
        return Movement.getRunEnergy() < 5
                || !Movement.isStaminaEnhancementActive();
    }

    public static void handleGate() {
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
