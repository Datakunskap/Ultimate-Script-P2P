package script.quests.nature_spirit.wrappers;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import script.data.Locations;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.tasks.fungus.Fungus;
import script.wrappers.BankWrapper;
import script.wrappers.GEWrapper;
import script.wrappers.SupplyMapWrapper;

import java.util.LinkedHashMap;

public class WalkingWrapper extends script.wrappers.WalkingWrapper {

    private static Area MORTANIA = Area.rectangular(3410, 3454, 3520, 3321);
    private static final Position GATE_POSITION = new Position(3443, 3459, 0);
    private static final Position AMULET_POSITION = new Position(3147, 3174, 0);
    ;

    public static void walkToNatureGrotto() {
        if (Location.NATURE_GROTTO_BRIDGE_POSITION.distance() > 4) {
            Log.fine("Walking To Nature Grotto");

            if (!MORTANIA.contains(Players.getLocal()) && !inSalveGravyardArea() && GATE_POSITION.distance() > 6) {
                if (Inventory.contains("Salve graveyard teleport") && GATE_POSITION.distance() > 10 && !Location.DUNGEON_AREA.contains(Players.getLocal())) {
                    Fungus.useTeleportTab("Salve graveyard teleport");
                }
                walkToPosition(GATE_POSITION);
            }

            Movement.walkTo(Location.NATURE_GROTTO_BRIDGE_POSITION,
                    () -> {
                        if (shouldBreakOnTarget() || inSalveGravyardArea()) {
                            if (inSalveGravyardArea()) {
                                if (Inventory.getCount(true, f -> f.containsAction("Drink") || f.containsAction("Eat")) < 3) {
                                    Log.info("Getting more foods");
                                    BankWrapper.doBanking(false, false, SupplyMapWrapper.getNatureSpiritKeepMap());
                                    return true;
                                } else {
                                    Log.fine("Handling Gate");
                                    Fungus.handleGate();
                                }
                            } else {
                                if (!Movement.isRunEnabled()) {
                                    Movement.toggleRun(true);
                                }
                            }
                        }
                        if (Players.getLocal().getHealthPercent() < 35 || Health.getPercent() < 35 || Inventory.isFull()) {
                            consumeFirstConsumable();
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

    private static void crossGrottoBridge(boolean toGrotto) {
        if (Players.getLocal().getHealthPercent() < 35) {
            consumeFirstConsumable();
        }
        if ((toGrotto && Location.NATURE_GROTTO_AREA.contains(Players.getLocal()))
                || (!toGrotto && !Location.NATURE_GROTTO_AREA.contains(Players.getLocal())))
                return;

        SceneObject bridge = SceneObjects.getNearest("Bridge");
        if (bridge != null && bridge.interact(a -> true)) {
            if (toGrotto) {
                if (!Time.sleepUntil(() -> Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 7_000)) {
                    Movement.setWalkFlag(Location.NATURE_GROTTO_AREA.getCenter());
                }
            } else {
                Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 7_000);
            }
        }
    }

    public static void enterGrotto() {
        if (Inventory.contains("Ghostspeak amulet")) {
            Inventory.getFirst("Ghostspeak amulet").interact(a -> true);
            Time.sleepUntil(() -> Equipment.contains("Ghostspeak amulet"), 5000);
        }
        if (!Equipment.contains("Ghostspeak amulet")) {
            Log.fine("Getting amulet of ghostspeak");

            if (Inventory.contains("Salve graveyard teleport") &&
                    (MORTANIA.contains(Players.getLocal()) || Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()))) {
                Fungus.useTeleportTab("Salve graveyard teleport");

            } else if (Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                exitAndLeaveGrotto();
            }
            while (!Equipment.contains("Ghostspeak amulet") && Game.isLoggedIn()) {
                if (AMULET_POSITION.distance() > 5) {
                    Log.info("Checking bank for Ghostspeak amulet");
                    BankWrapper.doBanking(false, false, SupplyMapWrapper.getNatureSpiritKeepMap());
                    if (!Inventory.contains("Ghostspeak amulet")) {
                        Log.info("Getting a new Ghostspeak amulet");
                        walkToPosition(AMULET_POSITION);
                    }
                }
                if (!Dialog.isOpen()) {
                    Npc man = Npcs.getNearest(n -> n.containsAction("Talk-to"));
                    if (man != null) {
                        man.interact("Talk-to");
                        Time.sleepUntil(Dialog::isOpen, 10_000);
                    }
                }
                if (Dialog.canContinue()) {
                    Dialog.processContinue();
                }
                Dialog.process(o -> o.contains("lost"));
                while (Dialog.isOpen()) {
                    Time.sleep(1200, 1800);
                    Dialog.processContinue();
                }
                if (Inventory.contains("Ghostspeak amulet")) {
                    Inventory.getFirst("Ghostspeak amulet").interact("Wear");
                    Time.sleepUntil(() -> Equipment.contains("Ghostspeak amulet"), 5000);
                }
                Time.sleep(1000);
            }
        } else {

            SceneObject grotto = SceneObjects.getNearest("Grotto");

            if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
                Time.sleepUntil(() -> Dialog.isOpen() && !Players.getLocal().isMoving(), 8000);
            }
        }
    }

    private static boolean inMortania() {
        return Fungus.BLOOM_TILE.distance() > 5 && Fungus.BLOOM_TILE.distance() < 100;
    }

    private static boolean inSalveGravyardArea() {
        Player local = Players.getLocal();
        return Fungus.AFTER_SALVE_GRAVEYARD_TELEPORT_AREA.contains(local);
    }

    public static void getSilverSickleB() {
        if (!Inventory.contains("Silver sickle") && !Inventory.contains("Silver sickle (b)")) {
            Log.info("Checking bank for sickle");
            if (Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()) || Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {
                WalkingWrapper.exitAndLeaveGrotto();
                return;
            } else {
                if (Quest.NATURE_SPIRIT.getVarpValue() >= 75) {
                    BankWrapper.doBanking(false, false, SupplyMapWrapper.getMortMyreFungusKeepMap());
                } else {
                    BankWrapper.doBanking(false, false, SupplyMapWrapper.getNatureSpiritKeepMap());
                }
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
                        if (Inventory.contains("Salve graveyard teleport")) {
                            Fungus.useTeleportTab("Salve graveyard teleport");
                        } else {
                            WalkingWrapper.exitAndLeaveGrotto();
                            Time.sleepUntilForDuration(() -> Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()), Random.nextInt(800, 1200), 10_000);
                            WalkingWrapper.exitAndLeaveGrotto();
                            Time.sleepUntilForDuration(() -> !Locations.NATURE_GROTTO_AREA.contains(Players.getLocal()), Random.nextInt(800, 1200), 10_000);
                            BankWrapper.doBanking(false, false, SupplyMapWrapper.getMortMyreFungusKeepMap());
                        }
                        BankWrapper.doBanking(false, false, SupplyMapWrapper.getMortMyreFungusKeepMap());
                    } else {
                        Movement.setWalkFlag(Locations.INSIDE_GROTTO_AREA.getCenter());
                    }
                } else if (!Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                    WalkingWrapper.walkToNatureGrotto();
                }
                WalkingWrapper.enterGrotto();
            } else {
                Log.info("Buying sickle");
                if (Quest.NATURE_SPIRIT.getVarpValue() >= 75) {
                    LinkedHashMap<String, Integer> map = new LinkedHashMap<>(SupplyMapWrapper.getMortMyreFungusItemsMap());
                    map.remove("Silver sickle (b)");
                    map.put("Silver sickle", 1);
                    GEWrapper.setBuySupplies(true, Skills.getLevel(Skill.PRAYER) >= 50, SupplyMapWrapper.getNatureSpiritItemsMap());
                } else {
                    GEWrapper.setBuySupplies(true, false, SupplyMapWrapper.getNatureSpiritItemsMap());
                }
            }
        }
    }
}
