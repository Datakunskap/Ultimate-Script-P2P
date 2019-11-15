package script.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.data.IDs;
import script.data.Strings;
import script.wrappers.WalkingWrapper;
import script.wrappers.SleepWrapper;

public class GetStartersGold extends Task {

    private static final int MULE_WORLD = 494;
    static final int AMOUNT_TO_RECEIVE = 2000000;

    private static final String MULE_FOR_STARTERS_GOLD = "2147 Emblems";

    private static final Position MULE_POSITION = BankLocation.GRAND_EXCHANGE.getPosition();

    @Override
    public boolean validate() {
        return Inventory.containsAll(IDs.TUTORIAL_ISLAND_ITEMS)
                && Inventory.containsOnly(IDs.TUTORIAL_ISLAND_ITEMS);
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        if (Dialog.canContinue()) {
            Log.info("I am continuing the dialog");
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Log.info("I am toggling run");
                Movement.toggleRun(true);
            }
        }

        if (Inventory.contains(x -> x.getName().contains(Strings.STAMINA_POTION))) {
            if (!Movement.isStaminaEnhancementActive()) {
                Item staminaPotion = Inventory.getFirst(x -> x.getName().contains(Strings.STAMINA_POTION));
                Log.info("I am drinking a stamina potion");
                if (staminaPotion.interact(Strings.DRINK_ACTION)) {
                    Time.sleepUntil(Movement::isStaminaEnhancementActive, SleepWrapper.mediumSleep1500());
                }
            }
        }

        if (Worlds.getCurrent() != MULE_WORLD) {
            Log.info("I am hopping to the mule world");
            if (WorldHopper.hopTo(MULE_WORLD)) {
                Time.sleepUntil(() -> !Game.isLoggedIn(), SleepWrapper.longSleep7500());
                Time.sleepUntil(Game::isLoggedIn, SleepWrapper.longSleep7500());
            }
        }

        if (Worlds.getCurrent() == MULE_WORLD) {
            if (MULE_POSITION.distance() > 15) {
                Log.info("I am walking to the mule");
                Movement.walkTo(MULE_POSITION, WalkingWrapper::shouldBreakOnTarget);
            }

            if (MULE_POSITION.distance() <= 15) {
                if (!Inventory.contains(Strings.COINS)) {
                    Player mule = Players.getNearest(MULE_FOR_STARTERS_GOLD);
                    if (mule != null) {
                        if (!Trade.isOpen()) {
                            Log.info("I am offering the mule to trade");
                            if (mule.interact(Strings.TRADE_ACTION)) {
                                Time.sleepUntil(Trade::isOpen, SleepWrapper.longSleep7500());
                            }
                        }
                        if (Trade.isOpen()) {
                            if (Trade.isOpen(false)) {
                                Log.info("The first trade screen is open");
                                if (Trade.hasOtherAccepted()) {
                                    Log.info("I am accepting first trade screen");
                                    if (Trade.accept()) {
                                        Time.sleepUntil(() -> Trade.isOpen(true), SleepWrapper.longSleep7500());
                                    }
                                }
                            }
                            if (Trade.isOpen(true)) {
                                Log.info("The second trade screen is open");
                                if (Trade.hasOtherAccepted()) {
                                    Log.info("I am accepting second trade screen");
                                    if (Trade.accept()) {
                                        Time.sleepUntil(() -> Inventory.contains(Strings.COINS), SleepWrapper.longSleep7500());
                                    }
                                }
                            }
                        }
                    }
                    if (mule == null) {
                        Log.info("I can't find the mule, I'll wait for him");
                        Time.sleepUntil(() -> Players.getNearest(MULE_FOR_STARTERS_GOLD) != null, SleepWrapper.longSleep7500());
                    }
                }
                if (Inventory.contains(Strings.COINS)) {
                    if (Inventory.getCount(true, Strings.COINS) >= AMOUNT_TO_RECEIVE) {
                        Log.info("I did receive enough starters gold");
                    }
                    if (Inventory.getCount(true, Strings.COINS) < AMOUNT_TO_RECEIVE) {
                        Log.info("I didn't receive enough starters gold");
                    }
                }
            }
        }

        return SleepWrapper.shortSleep350();
    }
}
