package script.tasks.fungus;

import api.component.ExWorldHopper;
import api.component.ExWorlds;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.*;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import script.wrappers.*;

import java.util.function.Predicate;

@ScriptMeta(developer = "Streagrem", name = "AntiPker", desc = "AntiPker")
public class Fungus extends Task {

    public static final Position BLOOM_TILE = new Position(3421, 3437);
    public static final Position CLAN_WARS_INSIDE_TILE = new Position(3327, 4751);
    public static final Area AFTER_SALVE_GRAVEYARD_TELEPORT_AREA = Area.rectangular(3418, 3471, 3451, 3458);
    public static final Predicate<Item> RESTOCK_TELEPORTS = x -> x.getName().contains("Varrock teleport");

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() >= 75
                && Skills.getLevel(Skill.PRAYER) >= 50;
    }

    @Override
    public int execute() {
        if (!Game.isLoggedIn() || Players.getLocal() == null)
            return 2000;

        RSWorld world = Worlds.getLocal();
        if(world != null && world.isMembers()){
            ExWorldHopper.randomInstaHopInPureP2p();
            return 2000;
        }

        if (!Bank.isOpen() && !Equipment.contains("Silver sickle (b)")) {
            if (Inventory.contains("Silver sickle (b)") && Inventory.getFirst("Silver sickle (b)").interact(a -> true)) {
                Log.info("Sickle equipped");
                Time.sleepUntil(() -> Equipment.contains("Silver sickle (b)"), 5000);
            } else {
                WalkingWrapper.getSilverSickleB();
                return SleepWrapper.mediumSleep1000();
            }
        }

        if (!atMortMyreFungusLogs() && !atClanWars() && !inMortania() && !insideClanWars()) {
            Log.info("idk");
            if (BankLocation.GRAND_EXCHANGE.getPosition().distance() < 15 && Inventory.contains(i -> i.getName().contains("Ring of dueling("))) {
                Inventory.getFirst(i -> i.getName().contains("Ring of dueling(")).interact("Rub");
                Time.sleepUntil(Dialog::isViewingChatOptions, 5000);
                Dialog.process(o -> o.contains("Clan Wars"));
                Time.sleepUntil(() -> BankLocation.GRAND_EXCHANGE.getPosition().distance() > 15 && !Game.isLoadingRegion(), 8000);
            }
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
            WorldhopWrapper.checkWorldHop();

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
            WorldhopWrapper.resetChecker();
            doBanking();
        }

        if (insideClanWars()) {
            useTeleportTab("Salve graveyard teleport");
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

    public static void handleGate() {
        Player local = Players.getLocal();
        SceneObject gate = SceneObjects.getNearest("Gate");
        Log.info("Opening the gate");
        if (gate != null) {
            if (gate.containsAction("Open")) {
                if (gate.interact("Open")) {
                    Time.sleepUntil(() -> !AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local) || Interfaces.getComponent(580, 17) != null, 4000);
                    InterfaceComponent dontAskMeThisAgain = Interfaces.getComponent(580, 20);
                    if (dontAskMeThisAgain != null && dontAskMeThisAgain.getMaterialId() == 941) {
                        Log.info("dontAskMeThisAgain is visible");
                        if (dontAskMeThisAgain.interact("Off/On")) {
                            Log.info("Clicked enterTheSwamp");
                            Time.sleepUntil(() -> dontAskMeThisAgain.getMaterialId() == 942, 2000);
                        }
                    }
                    InterfaceComponent enterTheSwamp = Interfaces.getComponent(580, 17);
                    if (enterTheSwamp != null) {
                        if (enterTheSwamp.getMaterialId() == 942) {
                            Log.info("enterTheSwamp is visible and dontAskMeAgain is toggled");
                            if (enterTheSwamp.interact("Yes")) {
                                Log.info("Clicked enterTheSwamp");
                                Time.sleepUntil(() -> !AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local), 4000);
                            }
                        }
                    }
                    if ((dontAskMeThisAgain == null || !dontAskMeThisAgain.isVisible() || dontAskMeThisAgain.isExplicitlyHidden())
                            && enterTheSwamp != null) {
                        Log.info("enterTheSwamp is visible, but dontAskMeAgain isn't");
                        if (enterTheSwamp.interact("Yes")) {
                            Log.info("Clicked enterTheSwamp");
                            Time.sleepUntil(() -> !AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local), 4000);
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
        if (fungi != null && fungi.distance() < 6) {
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

    public static void useTeleportTab(String tabName) {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            Interfaces.closeAll();
            Bank.close();
        }
        Time.sleepUntilForDuration(() -> !Bank.isOpen() && !GrandExchange.isOpen(), Random.nextInt(2000, 3000), 8000);
        Item teleportTab = Inventory.getFirst(tabName);
        if (teleportTab != null) {
            Log.info("Using a " + teleportTab.getName() + " tab");
            if (teleportTab.interact("Break")) {
                if (tabName.equalsIgnoreCase("Salve graveyard teleport")) {
                    Time.sleepUntil(() -> BLOOM_TILE.distance() < 100 && !Game.isLoadingRegion(), 5000);
                } else {
                    Time.sleepUntil(Game::isLoadingRegion, 2000);
                    Time.sleepUntil(() -> !Game.isLoadingRegion(), 6000);
                }
            }
        }
    }

    public void doBanking() {
        if (BankLocation.CLAN_WARS.getPosition().distance() > 50) {
            if (Inventory.containsAnyExcept(x -> x.getName().contains("Ring of dueling(") || x.getName().contains("Salve graveyard teleport"))) {
                if (Inventory.contains(x -> x.getName().contains("Ring of dueling(") && !x.isNoted())) {
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
                } else {
                    Log.info("Walking to clan wars since I don't have a dueling ring");
                    Movement.walkTo(BankLocation.CLAN_WARS.getPosition());
                }
            }
        }
        if (BankLocation.CLAN_WARS.getPosition().distance() <= 50) {
            if (!Inventory.contains(x -> x.getName().contains("Ring of dueling(")) || !Inventory.contains(x -> x.getName().contains("Salve graveyard teleport")) || Inventory.contains(x -> x.getName().contains("Ring of dueling(") && x.isNoted())) {
                if (!Bank.isOpen()) {
                    if (Bank.open()) {
                        Time.sleepUntil(() -> Bank.isOpen(), 20000);
                    }
                }
                if (Bank.isOpen()) {
                    if (Inventory.contains(x -> x.getName().contains("Ring of dueling(") && x.isNoted())) {
                        Bank.depositInventory();
                        Time.sleepUntil(Inventory::isEmpty, 8000);
                    }

                    if (!Inventory.contains(x -> x.getName().contains("Ring of dueling("))) {
                        if (Bank.contains(x -> x.getName().contains("Ring of dueling("))) {
                            if (Bank.withdraw(x -> x.getName().contains("Ring of dueling("), 1)) {
                                Time.sleepUntil(() -> Inventory.contains(x -> x.getName().contains("Ring of dueling(")), 5000);
                            }
                        }
                        if (!Bank.contains(x -> x.getName().contains("Ring of dueling(")) && !Inventory.contains(x -> x.getName().contains("Ring of dueling("))) {
                            //restock
                            Log.info("I need to restock ring of duelings");
                            withdrawGrandExchangeTeleport();
                            GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
                            return;
                        }
                    }
                    if (!Inventory.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                        if (!Bank.contains(x -> x.getName().contains("Salve graveyard teleport"))) {
                            //restock
                            Log.info("I need to restock salve graveyard teleports");
                            withdrawGrandExchangeTeleport();
                            GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
                            return;
                        }

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
                    Log.info("Deposting everything expect teleports");
                    if (Bank.depositAllExcept(x -> x.getName().contains("Ring of dueling(") && !x.isNoted() || x.getName().contains("Salve graveyard teleport"))) {
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

    public static void withdrawGrandExchangeTeleport() {
        if (Bank.isOpen() && Bank.contains(RESTOCK_TELEPORTS)) {
            Bank.withdraw(RESTOCK_TELEPORTS, 1);
            Time.sleepUntilForDuration(() -> Inventory.contains(RESTOCK_TELEPORTS), Random.nextInt(500, 800), 10_000);
        }
    }
}
