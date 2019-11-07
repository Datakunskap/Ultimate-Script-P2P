package script.tasks.fungus.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.tasks.fungus.data.Items;
import script.tasks.fungus.data.Locations;
import script.wrappers.MovementBreakerWrapper;
import script.wrappers.SleepWrapper;

public class Banking extends Task {

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        if (Locations.CLAN_WARS_AREA.contains(local)) {
            if(!Equipment.contains(Items.SILVER_SICKLE)
                    || !Inventory.contains(Items.RING_OF_DUELING)
                    || !Inventory.contains(Items.SALVE_GRAVEYARD_TELEPORT)
                    || Inventory.getFreeSlots() != 26){
                if(!Bank.isOpen()){
                    if(Bank.open()){
                        Time.sleepUntil(()-> Bank.isOpen(), SleepWrapper.longSleep7500());
                    }
                }
                if(Bank.isOpen()){
                    //fix invent
                }
            }
            if(Equipment.contains(Items.SILVER_SICKLE)
            && Inventory.contains(Items.RING_OF_DUELING)
            && Inventory.contains(Items.SALVE_GRAVEYARD_TELEPORT)
            && Inventory.getFreeSlots() == 26){
                SceneObject portal = SceneObjects.getNearest("Free-for-all portal");
                if(portal.interact(a -> true)){
                    Time.sleepUntil(()-> !Locations.CLAN_WARS_AREA.contains(local), SleepWrapper.longSleep7500());
                }
            }
        }

        if (Locations.MORYTANIA_AREA.contains(local)) {
            if (Inventory.isFull()) {
                //bank
            }
            if (!Inventory.isFull()) {
                if (Equipment.contains(Items.SILVER_SICKLE)) {
                    if (Skills.getCurrentLevel(Skill.PRAYER) > 0) {
                        if (Locations.BLOOM_TILE.distance() > 0) {
                            Movement.walkTo(Locations.BLOOM_TILE);
                        }
                        if (Locations.BLOOM_TILE.distance() == 0) {
                            //cast bloom & pick fungus
                        }
                    }
                }
                if (!Equipment.contains(Items.SILVER_SICKLE)) {
                    //bank
                }
            }
        }

        return SleepWrapper.shortSleep350();
    }
}
