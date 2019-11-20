package script.wrappers;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;

public class MovementBreaks {

    public static boolean shouldBreakOnRunenergy() {
        return !Movement.isStaminaEnhancementActive() && Movement.getRunEnergy() < 5
                || Movement.isStaminaEnhancementActive() && Movement.getRunEnergy() < 5
                || !Movement.isRunEnabled() && Movement.getRunEnergy() >= 5;
    }

    public static boolean shouldBreakOnTarget() {
        Npc attacker = Npcs.getNearest(a -> true);
        Player local = Players.getLocal();
        return local.isHealthBarVisible()
                && attacker != null
                && attacker.getTarget() != null
                && attacker.getTarget().equals(local)
                || !Movement.isStaminaEnhancementActive() && Movement.getRunEnergy() < Random.mid(5, 20)
                || Movement.isStaminaEnhancementActive() && Movement.getRunEnergy() < Random.mid(5, 10)
                || !Movement.isRunEnabled() && Movement.getRunEnergy() > Random.mid(5, 25);
    }

}
