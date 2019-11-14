package script.quests.nature_spirit;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.tasks.*;
import script.quests.nature_spirit.wrappers.WalkingWrapper;


@ScriptMeta(name = "Nature Spirit", desc = "Nature Spirit", developer = "DrScatman")
public class NatureSpirit {

    public static final Task[] TASKS = {
                new NatureSpirit0(),
                new NatureSpirit1(),
                new NatureSpirit2(),
                new NatureSpirit3(),
                new NatureSpirit4(),
                new NatureSpirit5(),
                new NatureSpirit6(),
                new NatureSpirit7(),
                new NatureSpirit8(),
                new NatureSpirit9(),
                new NatureSpirit10()
    };

    public static int getLoopReturn() {
        return Random.low(650, 1600);
    }

    public static boolean useItemOnObject(String itemName, String objectName) {
        return useItemOnObject(itemName, SceneObjects.getNearest(objectName).getId());
    }

    public static boolean useItemOnObject(String itemName, Position objectPosition) {
        return useItemOnObject(itemName, SceneObjects.getFirstAt(objectPosition).getId());
    }

    public static boolean useItemOnObject(String itemName, int objectID) {
        Item item = Inventory.getFirst(a -> a.getName().equalsIgnoreCase(itemName));
        if (item != null && (item.interact("Use") || item.interact(ActionOpcodes.ITEM_ACTION_0) || item.click())) {
            Time.sleepUntil(Inventory::isItemSelected, 5000);
            Time.sleep(300, 600);
            SceneObject object = SceneObjects.getNearest(objectID);
            if (object != null && (object.interact(ActionOpcodes.ITEM_ON_OBJECT) || object.click())) {
                Time.sleepUntil(() -> Players.getLocal().isAnimating() && !Inventory.isItemSelected(), 5000);
                Time.sleepUntil(() -> !Players.getLocal().isAnimating() && !Players.getLocal().isMoving(), 5000);
                return true;
            }
        }
        return false;
    }

    public static void doFungusPicking() {
        SceneObject fungiLog = SceneObjects.getNearest(3509);

        if (fungiLog != null && !Inventory.contains("Mort myre fungus")) {
            if (fungiLog.interact("Pick")) {
                Time.sleepUntil(() -> Inventory.contains("Mort myre fungus"), 5000);
            } else {
                Log.severe("Cant Pick Fungi");
            }
            fungiLog = SceneObjects.getNearest(3509);
            Time.sleep(300, 600);
        }

        if (fungiLog == null && !Inventory.contains(i -> i.getName().equalsIgnoreCase("Druidic spell"))
                && !Inventory.contains("Mort myre fungus")) {

            if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                WalkingWrapper.walkToNatureGrotto();
            } else {
                SceneObject grotto = SceneObjects.getNearest("Grotto");

                if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
                    Time.sleepUntil(Dialog::isOpen, 5000);
                }

                if (Dialog.isOpen()) {
                    if (Dialog.canContinue()) {
                        Dialog.processContinue();
                    }

                    if (Dialog.isViewingChatOptions()) {
                        Dialog.process("Could I have another bloom scroll please?");
                    }
                }
            }
        }

        if (fungiLog == null && Inventory.contains(i -> i.getName().equalsIgnoreCase("Druidic spell"))
                && !Inventory.contains("Mort myre fungus")) {

            if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                SceneObject bridge = SceneObjects.getNearest("Bridge");

                if (bridge != null && !Players.getLocal().isMoving() && bridge.interact(a -> true)) {
                    Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
                }
            }

            if (Location.ROTTING_LOG_POSITION.distance() > 1) {
                script.wrappers.WalkingWrapper.walkToPosition(Location.ROTTING_LOG_POSITION);
            }

            SceneObject log = SceneObjects.getNearest("Rotting log");
            Item spell = Inventory.getFirst("Druidic spell");

            if (SceneObjects.getNearest(3509) == null && log != null && log.getPosition().equals(Players.getLocal().getPosition())) {
                Movement.walkTo(Location.ROTTING_LOG_POSITION.translate(Random.nextInt(-1, 1), Random.nextInt(-1, 1)));
            }

            if (log != null && log.distance() <= 1 && spell != null && spell.interact(ActionOpcodes.ITEM_ACTION_0)) {

                Time.sleepUntil(() -> SceneObjects.getNearest(3509) != null, 6000);
                Time.sleep(600, 800);

            }
        }
    }
}
