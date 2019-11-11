package script.tasks.fungus;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.data.Locations;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import script.wrappers.BankWrapper;
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;
import script.wrappers.SupplyMapWrapper;

import java.util.HashMap;

@ScriptMeta(developer = "Streagrem", name = "AntiPker", desc = "AntiPker")
public class Fungus extends Task {

    public static final Position BLOOM_TILE = new Position(3421, 3437);
    public static final Position CLAN_WARS_INSIDE_TILE = new Position(3327, 4751);
    public static final Area AFTER_SALVE_GRAVEYARD_TELEPORT_AREA = Area.rectangular(3418, 3471, 3451, 3458);

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() >= 75
                && Skills.getLevel(Skill.PRAYER) >= 50;
    }

    @Override
    public int execute() {
        if (!Game.isLoggedIn() || Players.getLocal() == null)
            return 2000;

        if (!Equipment.contains("Silver sickle (b)")) {
            if (Inventory.contains("Silver sickle (b)") && Inventory.getFirst("Silver sickle (b)").interact(a -> true)) {
                Log.info("Sickle equipped");
                Time.sleepUntil(() -> Equipment.contains("Silver sickle (b)"), 5000);
            } else {
                getSilverSickleB();
                return SleepWrapper.shortSleep600();
            }
        }

        if (!atMortMyreFungusLogs() && !atClanWars() && !inMortania() && !insideClanWars()) {
            Log.info("idk");
            Movement.walkTo(BankLocation.CLAN_WARS.getPosition());
        }

        if (inMortania()) {
            if (inSalveGravyardArea()) {
                handleGate();
            }
            if (!inSalveGravyardArea()) {
                Movement.walkTo(BLOOM_TILE);
            }
        }

        if (atMortMyreFungusLogs()) {
            if (!outOfPrayer()) {
                if (!Inventory.isFull()) {
                    collecingFungi();
                }
            }
            if (outOfPrayer()) {
                collectingLastFungi();
            }
            if (Inventory.isFull()) {
                doBanking();
            }
        }

        if (atClanWars()) {
            doBanking();
        }

        if (insideClanWars()) {
            useSalveGraveyardTeleport();
        }

        return Random.mid(299, 399);
    }

    public boolean insideClanWars() {
        return CLAN_WARS_INSIDE_TILE.distance() < 10;
    }

    public boolean atMortMyreFungusLogs() {
        return BLOOM_TILE.distance() <= 5;
    }

    public static boolean inMortania() {
        return BLOOM_TILE.distance() > 5 && BLOOM_TILE.distance() < 100;
    }

    public static boolean inSalveGravyardArea() {
        Player local = Players.getLocal();
        return AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local);
    }

    public boolean atClanWars() {
        return BankLocation.CLAN_WARS.getPosition().distance() < 50;
    }

    public boolean outOfPrayer() {
        return Skills.getCurrentLevel(Skill.PRAYER) == 0;
    }

    public void handleGate() {
        Player local = Players.getLocal();
        SceneObject gate = SceneObjects.getNearest("Gate");
        InterfaceComponent enterTheSwamp = Interfaces.getComponent(580, 17);
        InterfaceComponent dontAskMeThisAgain = Interfaces.getComponent(580, 20);
        Log.info("Opening the gate");
        if (gate != null) {
            if (gate.containsAction("Open")) {
                if (gate.interact("Open")) {
                    Time.sleepUntil(() -> !AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local) || enterTheSwamp != null, 30_000);
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
                                Time.sleepUntil(() -> !AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local), 5000);
                            }
                        }
                    }
                    if (dontAskMeThisAgain == null && enterTheSwamp != null) {
                        Log.info("enterTheSwamp is visible, but dontAskMeAgain isn't");
                        if (enterTheSwamp.interact("Yes")) {
                            Log.info("Clicked enterTheSwamp");
                            Time.sleepUntil(() -> !AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local), 5000);
                        }
                    }
                }
            }
        }
    }

    public void collecingFungi() {
        SceneObject fungi = SceneObjects.getNearest(3509);
        if (fungi == null) {
            Log.info("There is no fungi to pick");
            if (BLOOM_TILE.distance() == 0) {
                Log.info("I am standing on the right tile to cast bloom");
                Log.info("Casting bloom");
                int prayerPointsBefore = Skills.getCurrentLevel(Skill.PRAYER);
                if (EquipmentSlot.MAINHAND.interact("Bloom")) {
                    Time.sleepUntil(() -> prayerPointsBefore != Skills.getCurrentLevel(Skill.PRAYER), 5000);
                }
            }
            if (BLOOM_TILE.distance() > 0) {
                Log.info("I am not standing on the right tile to cast bloom");
                Log.info("Walking to the bloom tile");
                Movement.setWalkFlag(BLOOM_TILE);
                Time.sleepUntil(() -> BLOOM_TILE.distance() == 0, 5000);
            }
        }
        if (fungi != null) {
            Log.info("Picking fungi");
            int fungiAmountBefore = Inventory.getCount("Mort myre funus");
            if (fungi.interact("Pick")) {
                Time.sleepUntil(() -> fungiAmountBefore < Inventory.getCount("Mort myre fungus"), 5000);
                BankWrapper.updateInventoryValue();
            }
        }
    }

    public void collectingLastFungi() {
        SceneObject fungi = SceneObjects.getNearest(3509);
        if (fungi != null) {
            if (!Inventory.isFull()) {
                Log.info("Picking fungi");
                int fungiAmountBefore = Inventory.getCount("Mort myre funus");
                if (fungi.interact("Pick")) {
                    Time.sleepUntil(() -> fungiAmountBefore < Inventory.getCount("Mort myre fungus"), 5000);
                    BankWrapper.updateInventoryValue();
                }
            }
        }
        if (fungi == null) {
            Log.info("I need to bank");
            doBanking();
        }
    }

    public static void useSalveGraveyardTeleport() {
        Time.sleep(1000, 1299);
        Item salveGraveyardTeleport = Inventory.getFirst("Salve graveyard teleport");
        if (salveGraveyardTeleport != null) {
            Log.info("Using a tab to teleport to salve graveyard teleport");
            if (salveGraveyardTeleport.interact("Break")) {
                Time.sleepUntil(() -> BLOOM_TILE.distance() < 100 && !Game.isLoadingRegion(), 5000);
            }
        }
    }

    public void doBanking() {
        if (BankLocation.CLAN_WARS.getPosition().distance() > 50) {
            if (Inventory.containsAnyExcept(x -> x.getName().contains("Ring of dueling(") || x.getName().contains("Salve graveyard teleport"))) {
                if (Inventory.contains(x -> x.getName().contains("Ring of dueling("))) {
                    if (!Dialog.isViewingChatOptions()) {
                        Item row = Inventory.getFirst(x -> x.getName().contains("Ring of dueling("));
                        Log.info("Rubbing the dueling ring to teleport to clan wars");
                        if (row.interact("Rub")) {
                            Time.sleepUntil(() -> Dialog.isViewingChatOptions(), 5000);
                        }
                    }
                    if (Dialog.isViewingChatOptions()) {
                        Log.info("Selecting teleport to clan wars teleport option");
                        if (Dialog.process("Clan Wars Arena.")) {
                            Time.sleepUntil(() -> BankLocation.CLAN_WARS.getPosition().distance() <= 50, 5000);
                        }
                    }
                }
                if (!Inventory.contains(x -> x.getName().contains("Ring of dueling("))) {
                    Log.info("Walking to clan wars since I don't have a dueling ring");
                    Movement.walkTo(BankLocation.CLAN_WARS.getPosition());
                }
            }
        }
        if (BankLocation.CLAN_WARS.getPosition().distance() <= 50) {
            if (!Inventory.contains(x -> x.getName().contains("Ring of dueling(") && x.getName().contains("Salve graveyard teleport"))) {
                if (!Bank.isOpen()) {
                    if (Bank.open()) {
                        Time.sleepUntil(() -> Bank.isOpen(), 20000);
                    }
                }
                if (Bank.isOpen()) {
                    if (!Inventory.contains(x -> x.getName().contains("Ring of dueling("))) {
                        if (Bank.contains(x -> x.getName().contains("Ring of dueling("))) {
                            if (Bank.withdraw(x -> x.getName().contains("Ring of dueling("), 1)) {
                                Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of dueling(")), 5000);
                            }
                        }
                        if (!Bank.contains(x -> x.getName().contains("Ring of dueling("))) {
                            //restock
                            Log.info("I need to restock ring of duelings");
                            if (Bank.contains(x -> x.getName().contains("Ring of wealth ("))) {
                                Bank.withdraw(x -> x.getName().contains("Ring of wealth ("), 1);
                                Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of wealth (")), 5000);
                            }
                            GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
                        }
                    }
                    if (!Inventory.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                        if (!Bank.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                            //restock
                        }
                        Log.info("I need to restock salve graveyard teleports");
                        if (Bank.contains(x -> x.getName().contains("Ring of wealth ("))) {
                            Bank.withdraw(x -> x.getName().contains("Ring of wealth ("), 1);
                            Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of wealth (")), 5000);
                        }
                        GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
                        if (Bank.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                            if (Bank.withdrawAll(x -> x.getName().contains("Salve graveyard teleport"))) {
                                Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Salve graveyard teleport")), 5000);
                            }
                        }
                    }
                    BankWrapper.updateBankValue();
                    BankWrapper.updateInventoryValue();
                }
            }
            if (Inventory.containsAnyExcept(x -> x.getName().contains("Ring of dueling(") || x.getName().contains("Salve graveyard teleport"))) {
                if (!Bank.isOpen()) {
                    Log.info("Opening the bank");
                    if (Bank.open()) {
                        Time.sleepUntil(() -> Bank.isOpen(), 10000);
                    }
                }
                if (Bank.isOpen()) {
                    if (!Inventory.contains(x -> x.getName().contains("Ring of dueling(") && x.getName().contains("Salve graveyard teleport"))) {
                        if (!Inventory.contains(x -> x.getName().contains("Ring of dueling("))) {
                            if (Bank.contains(x -> x.getName().contains("Ring of dueling("))) {
                                if (Bank.withdraw(x -> x.getName().contains("Ring of dueling("), 1)) {
                                    Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of dueling(")), 5000);
                                }
                            }
                            if (!Bank.contains(x -> x.getName().contains("Ring of dueling("))) {
                                //restock
                                Log.info("I need to restock ring of duelings");
                                if (Bank.contains(x -> x.getName().contains("Ring of wealth ("))) {
                                    Bank.withdraw(x -> x.getName().contains("Ring of wealth ("), 1);
                                    Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of wealth (")), 5000);
                                }
                                GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
                            }
                        }
                        if (!Inventory.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                            if (Bank.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                                if (Bank.withdrawAll(x -> x.getName().contains("Salve graveyard teleport"))) {
                                    Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Salve graveyard teleport")), 5000);
                                }
                            }
                            if (!Bank.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                                //restock
                                Log.info("I need to restock salve graveyard teleports");
                                if (Bank.contains(x -> x.getName().contains("Ring of wealth ("))) {
                                    Bank.withdraw(x -> x.getName().contains("Ring of wealth ("), 1);
                                    Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of wealth (")), 5000);
                                }
                                GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
                            }
                        }
                    }
                    Log.info("Deposting everythign expect teleports");
                    if (Bank.depositAllExcept(x -> x.getName().contains("Ring of dueling(") || x.getName().contains("Salve graveyard teleport"))) {
                        Time.sleepUntil(() -> Inventory.containsOnly(x -> x.getName().contains("Ring of dueling(") || x.getName().contains("Salve graveyard teleport")), 5000);
                    }
                    BankWrapper.updateBankValue();
                    BankWrapper.updateInventoryValue();
                }
            }
            if (Inventory.contains(x -> x.getName().contains("Ring of dueling("))
                    && Inventory.contains(i -> i.getName().contains("Salve graveyard teleport"))
                    && Inventory.getFreeSlots() == 26) {
                SceneObject portal = SceneObjects.getNearest("Free-for-all portal");
                if (portal != null) {
                    Log.info("Entering clan wars portal to regain hitpoints, prayer points and run energy");
                    if (portal.interact("Enter")) {
                        Time.sleepUntil(() -> BankLocation.CLAN_WARS.getPosition().distance() > 50, 20000);
                    }
                }
                if (portal == null) {
                    Log.info("Can't find the portal");
                }
            }
        }
    }

    public static void getSilverSickleB() {
        if (!Inventory.contains("Silver sickle") && !Inventory.contains("Silver sickle (b)")) {
            Log.info("Checking bank for sickle");
            if (Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()) || Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {
                WalkingWrapper.exitAndLeaveGrotto();
                return;
            } else {
                BankWrapper.openAndDepositAll(false, false, SupplyMapWrapper.getMortMyreFungusItemsMap().keySet());
            }
        }
        if (!Inventory.contains("Silver sickle (b)")) {
            if (Bank.isOpen() && Bank.contains("Silver sickle")) {
                Bank.withdraw("Silver sickle", 1);
                Time.sleepUntil(() -> Inventory.contains("Silver sickle"), 2000);
            }
            if (Inventory.contains("Silver sickle")) {
                Log.info("Blessing sickle");
                if (Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {
                    NatureSpirit.useItemOnObject("Silver sickle", 3520);
                    if (Time.sleepUntil(() -> Inventory.contains("Silver sickle (b)"), 8000)) {
                        Log.fine("Sickle Blessed!");
                        WalkingWrapper.exitAndLeaveGrotto();
                    } else {
                        Movement.setWalkFlag(Locations.INSIDE_GROTTO_AREA.getCenter());
                    }
                } else if (!Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                    WalkingWrapper.walkToNatureGrotto();
                }
                WalkingWrapper.enterGrotto();
            } else {
                Log.info("Buying sickle");
                HashMap<String, Integer> map = new HashMap<>(SupplyMapWrapper.getMortMyreFungusItemsMap());
                map.remove("Silver sickle (b)");
                map.put("Silver sickle", 1);
                GEWrapper.setBuySupplies(true, true, map);
            }
        }
    }
}
