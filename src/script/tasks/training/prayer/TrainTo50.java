package script.tasks.training.prayer;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.priest_in_peril.data.Quest;
import script.wrappers.SleepWrapper;

import java.util.function.Predicate;


public class TrainTo50 extends Task {

    public static final String DRAGON_BONES = "Dragon bones";

    Predicate<Item> dragonBones = x -> x.getName().contains("Dragon bones");
    Predicate<Item> glory = x -> x.getName().contains("Amulet of glory(");
    Predicate<Item> burningAmulet = x -> x.getName().contains("Burning amulet(");

    public static final Position TEMPLE_POSITION = new Position(2950, 3820, 0);
    public static final Area WILDERNESS_TELEPORT_AREA = Area.rectangular(3008, 3849, 3046, 3815);
    public static final Area WILDERNESS_AREA = Area.rectangular(2936, 3854, 3050, 3796);


    @Override
    public boolean validate() {
        return Quest.PRIEST_IN_PERIL.getVarpValue() == 61
                && Skills.getLevel(Skill.PRAYER) < 50;
    }

    @Override
    public int execute() {

        Log.info("Prayer training");

        Player local = Players.getLocal();

        Player pker = Players.getNearest();

        if (pker != null) {
            if (pker.getCombatLevel() <= 75) {
                Log.info("There is another player nearby in our combat range");
            }
            if (pker.getCombatLevel() > 75) {
                Log.info("There is another player nearby out our combat range");
            }
        }

        if (pker == null) {
            Log.info("There is no other player nearby");
        }

        if (BankLocation.LUMBRIDGE_CASTLE.getPosition().distance() < 50) {
            if (WorldHopper.randomHopInP2p()) {
                Time.sleepUntil(() -> Game.isLoggedIn(), SleepWrapper.longSleep7500());
                if (Movement.walkTo(BankLocation.EDGEVILLE.getPosition())) {
                    Time.sleepUntil(() -> BankLocation.EDGEVILLE.getPosition().distance() < 15, 5000);
                }
            }
        }

        if (TEMPLE_POSITION.distance() > 10) {
            if (!Inventory.contains(dragonBones)
                    || !Inventory.contains(glory)
                    || !Inventory.contains(burningAmulet)) {
                if (BankLocation.EDGEVILLE.getPosition().distance() > 15) {
                    Movement.walkTo(BankLocation.EDGEVILLE.getPosition());

                }
                if (BankLocation.EDGEVILLE.getPosition().distance() <= 15) {
                    if (!Bank.isOpen()) {
                        if (Bank.open()) {
                            Time.sleepUntil(Bank::isOpen, SleepWrapper.longSleep7500());
                        }
                    }
                    if (Bank.isOpen()) {
                        if (Inventory.contains("Amulet of glory")) {
                            if (Bank.depositAll("Amulet of glory")) {
                                Time.sleepUntil(() -> !Inventory.contains("Amulet of glory"), SleepWrapper.longSleep7500());
                            }
                        }
                        if (!Inventory.contains(glory)) {
                            Log.info("Withrawing glory");
                            if (Bank.contains(glory)) {
                                if (Bank.withdraw(glory, 1)) {
                                    Time.sleepUntil(() -> Inventory.contains(glory), SleepWrapper.longSleep7500());
                                }
                            }
                        }
                        if (!Inventory.contains(burningAmulet)) {
                            if (Bank.contains(burningAmulet)) {
                                Log.info("Withrawing burning amulet");
                                if (Bank.withdraw(burningAmulet, 1)) {
                                    Time.sleepUntil(() -> Inventory.contains(burningAmulet), SleepWrapper.longSleep7500());
                                }
                            }
                        }
                        if (!Inventory.contains(dragonBones)) {
                            if (Bank.contains(dragonBones)) {
                                Log.info("Withrawing dragon bones");
                                if (Bank.withdraw(dragonBones, 26)) {
                                    Time.sleepUntil(() -> Inventory.contains(dragonBones), SleepWrapper.longSleep7500());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Inventory.contains(dragonBones)
                && Inventory.contains(glory)
                && Inventory.contains(burningAmulet)) {
            if (!WILDERNESS_AREA.contains(local)) {
                if (!Dialog.isViewingChatOptions()) {
                    if (Inventory.getFirst(burningAmulet).interact("Rub")) {
                        Time.sleepUntil(() -> Dialog.isViewingChatOptions(), SleepWrapper.longSleep7500());
                    }
                }
                if (Dialog.isViewingChatOptions()) {
                    if (Dialog.process("Lava Maze")) {
                        Time.sleepUntil(() -> Dialog.getChatOption(x -> x.contains("Okay, teleport to level 41 Wilderness.")).isVisible(), SleepWrapper.longSleep7500());
                    }
                    if (Dialog.process("Okay, teleport to level 41 Wilderness.")) {
                        Time.sleepUntil(() -> WILDERNESS_AREA.contains(local), SleepWrapper.longSleep7500());
                    }
                }
            }
        }

        if (Inventory.contains(dragonBones)
                && Inventory.contains(glory)
                && WILDERNESS_AREA.contains(local)) {

            if (WILDERNESS_TELEPORT_AREA.contains(local)) {
                Movement.walkToRandomized(new Position(2983, 3819, 0));
            }
            if (!WILDERNESS_TELEPORT_AREA.contains(local)) {
                if (TEMPLE_POSITION.distance() > 5) {
                    Movement.walkTo(TEMPLE_POSITION);
                }
                if (TEMPLE_POSITION.distance() <= 5) {
                    Item bones = Inventory.getFirst(dragonBones);
                    SceneObject altar = SceneObjects.getNearest("Chaos altar");
                    if (altar != null) {
                        if (bones != null) {
                            if (Dialog.canContinue()) {
                                Dialog.processContinue();
                            }
                            Log.info("1");
                            int countBefore = Skills.getExperience(Skill.PRAYER);
                            if (Inventory.use(x -> x.getName().equals("Dragon bones"), altar)) {
                                Log.info("2");
                                Time.sleepUntil(() -> countBefore != Skills.getExperience(Skill.PRAYER), SleepWrapper.mediumSleep1500());
                            }
                        }
                    }
                }
            }
        }

        if (TEMPLE_POSITION.distance() <= 10
                && Inventory.contains(glory)
                && !Inventory.contains(dragonBones)) {
            Pickable wine = Pickables.getNearest("Wine of zamorak");
            if (Health.getCurrent() != 0) {
                wine.interact("Take");
                Time.sleep(300, 500);
            }
        }


        return SleepWrapper.shortSleep350();
    }
}
