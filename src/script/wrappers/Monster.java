/*
package script.wrappers;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.scene.Players;

import javax.xml.crypto.dsig.Transform;

public class Monster {
    // Reference of AI Tier from AI thingy-mo-bob
    public Integer AI_TIER = 1;
    // Drag-n-drop Transform reference in Unity.
    public Transform monsterFullBodytransform;
    public Transform monsterHeadTransform;
    public Transform monsterNeckDownTransform;
    public Transform monsterWaistHingeTransform;

    public void start() {
        //hmm
    }

    public void update() {
        // null check tier?
        if (AI_TIER < 1 || monsterHeadTransform == null)
            return;

        // here or as field referencing player/player-transform?
        Player player = Players.getLocal();

        if (AI_TIER == 1) {
            // if within view distance of player
            if (monsterFullBodytransform.distance(player) < 10) {

                // if monster is facing player and player is facing monster
                if (player.isFacing(monsterFullBodytransform) && monsterHeadTransform.isFacing(player)) {

                }
                // TODO: and flashlight
                // if player is facing monster snap monster head and glare down player then slowly turn body and start charging
                if (player.isFacing(monsterFullBodytransform) && !monsterHeadTransform.isFacing(player)) {
                    // snap head not instant, but fast... needs some positional transitioning?
                    monsterHeadTransform.transformFast(new Quaternian(x, y, z, ...));
                    Animator.SetLookAtPosition(player.position);
                    Animator.SetLookAtWeight(1f);
                    // necessary?
                    Time.sleepUntil(() -> monsterHeadTransform.isFacing(player), 10_000);

                    // slowly turn body and stand upright facing player
                    monsterNeckDownTransform.transformSlow(new Quaternian(x, y, z, ...));

                    // make stand upright... no clue how this should look
                    if (monsterWaistHingeTransform.angleDegree() < 180 || monsterWaistHingeTransform.angleDegree() > 0) {
                        monsterWaistHingeTransform.transform(new Angle(0, 180, 0));
                    }
                }
            }
        }
    }
}
*/
