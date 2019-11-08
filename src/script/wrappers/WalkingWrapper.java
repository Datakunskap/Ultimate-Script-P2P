package script.wrappers;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import script.data.Locations;

public class WalkingWrapper {

    public static boolean shouldBreakOnTarget() {
        Npc attacker = Npcs.getNearest(a -> true);
        return Movement.getRunEnergy() > 0
                && !Movement.isRunEnabled()
                && Players.getLocal().isHealthBarVisible()
                && attacker != null
                && attacker.getTarget() != null
                && attacker.getTarget().equals(Players.getLocal());
    }

    public static boolean shouldBreakOnRunenergy() {
        return Movement.getRunEnergy() < 5
                || !Movement.isStaminaEnhancementActive();
    }

    public static void walkToNatureGrotto() {
        if (Locations.NATURE_GROTTO_BRIDGE_POSITION.distance() > 3) {
            Movement.walkTo(Locations.NATURE_GROTTO_BRIDGE_POSITION, WalkingWrapper::shouldBreakOnTarget);

            if (Locations.NATURE_GROTTO_BRIDGE_POSITION.distance() > 3 && !Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }
        } else {
            SceneObject bridge = SceneObjects.getNearest("Bridge");
            if (bridge != null && !Players.getLocal().isMoving() && bridge.interact(a -> true)) {
                Time.sleepUntil(() -> Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
            }
        }
    }

    public static void enterGrotto() {
        SceneObject grotto = SceneObjects.getNearest("Grotto");

        if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
            Time.sleepUntil(() -> Dialog.isOpen() && !Players.getLocal().isMoving(), 5000);
        }
    }
}
