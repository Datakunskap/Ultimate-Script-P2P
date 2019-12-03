package api;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.local.Health;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import script.wrappers.MovementBreaks;

public class API {

    public static final String STAMINA_POTION = "Stamina potion(";

    public static final String TALK_TO_ACTION = "Talk-to";
    public static final String WEAR_ACTION = "Wear";
    public static final String WIELD_ACTION = "Wield";
    public static final String DRINK_ACTION = "Drink";
    public static final String EAT_ACTION = "Eat";

    public static void drinkStaminaPotion() {
        if (Movement.getRunEnergy() < 5) {
            Log.info("I should drink a stamina potion");
            if (Inventory.contains(x -> x.getName().contains(STAMINA_POTION))) {
                Log.info("I do have a stamina potion in my inventory");
                if (!Dialog.isOpen()) {
                    Item staminaPotion = (Inventory.getFirst(x -> x.getName().contains(STAMINA_POTION)));
                    Log.info("I am drinking a stamina potion");
                    staminaPotion.interact(DRINK_ACTION);
                    Time.sleepUntil(() -> Movement.isStaminaEnhancementActive(), 2000);
                }
            }
        }
    }

    public static void useItemOn(String itemName, String interactableTarget, Position interactablePosition) {
        Interactable target = SceneObjects.getNearest(interactableTarget);
        if (interactablePosition.distance() > 3) {
            Log.info("Walking to" + " " + interactableTarget);
            Movement.walkTo(interactablePosition, MovementBreaks::shouldBreakOnRunenergy);
        }
        if (interactablePosition.distance() <= 3) {
            if (Inventory.contains(itemName)) {
                if (!Inventory.isItemSelected()) {
                    Inventory.getFirst(itemName).interact("Use");
                    Time.sleep(API.mediumRandom());
                }
                if (Inventory.isItemSelected()) {
                    if (target != null) {
                        if (target.interact("Use")) {
                            Time.sleep(API.mediumRandom());
                        }
                    }
                }
            }
        }
    }

    public static void useItemOn(int itemNameID, int interactableTargetID, Position interactablePosition) {
        Interactable target = SceneObjects.getNearest(interactableTargetID);
        if (interactablePosition.distance() > 3) {
            Log.info("Walking to" + " " + interactableTargetID);
            Movement.walkTo(interactablePosition, MovementBreaks::shouldBreakOnRunenergy);
        }
        if (interactablePosition.distance() <= 3) {
            if (Inventory.contains(itemNameID)) {
                if (!Inventory.isItemSelected()) {
                    Inventory.getFirst(itemNameID).interact("Use");
                    Time.sleep(API.mediumRandom());
                }
                if (Inventory.isItemSelected()) {
                    if (target != null) {
                        if (target.interact("Use")) {
                            Time.sleep(API.mediumRandom());
                        }
                    }
                }
            }
        }
    }

    public static void useItemOn(String itemName, int interactableTargetID, Position interactablePosition) {
        Interactable target = SceneObjects.getNearest(interactableTargetID);
        if (interactablePosition.distance() > 3) {
            Log.info("Walking to" + " " + interactableTargetID);
            Movement.walkTo(interactablePosition, MovementBreaks::shouldBreakOnRunenergy);
        }
        if (interactablePosition.distance() <= 3) {
            if (Inventory.contains(itemName)) {
                if (!Inventory.isItemSelected()) {
                    Inventory.getFirst(itemName).interact("Use");
                    Time.sleep(API.mediumRandom());
                }
                if (Inventory.isItemSelected()) {
                    if (target != null) {
                        if (target.interact("Use")) {
                            Time.sleep(API.mediumRandom());
                        }
                    }
                }
            }
        }
    }

    public static void useItemOn(int itemNameID, String interactableTargetName, Position interactablePosition) {
        Interactable target = SceneObjects.getNearest(interactableTargetName);
        if (interactablePosition.distance() > 3) {
            Log.info("Walking to" + " " + interactableTargetName);
            Movement.walkTo(interactablePosition, MovementBreaks::shouldBreakOnRunenergy);
        }
        if (interactablePosition.distance() <= 3) {
            if (Inventory.contains(itemNameID)) {
                if (!Inventory.isItemSelected()) {
                    Inventory.getFirst(itemNameID).interact("Use");
                    Time.sleep(API.mediumRandom());
                }
                if (Inventory.isItemSelected()) {
                    if (target != null) {
                        if (target.interact("Use")) {
                            API.mediumRandom();
                        }
                    }
                }
            }
        }
    }

    public static void useItemOn(String itemName, String interactableTarget) {
        Interactable target = SceneObjects.getNearest(interactableTarget);
        if (Inventory.contains(itemName)) {
            if (!Inventory.isItemSelected()) {
                Inventory.getFirst(itemName).interact("Use");
                Time.sleep(API.mediumRandom());
            }
            if (Inventory.isItemSelected()) {
                if (target != null) {
                    if (target.interact("Use")) {
                        Time.sleep(API.mediumRandom());
                    }
                }
            }
        }
    }

    public static void useItemOn(int itemName, SceneObject interactableTarget) {
        if (Inventory.contains(itemName)) {
            if (!Inventory.isItemSelected()) {
                Inventory.getFirst(itemName).interact("Use");
                Time.sleep(API.mediumRandom());
            }
            if (Inventory.isItemSelected()) {
                if (interactableTarget != null) {
                    if (interactableTarget.interact("Use")) {
                        Time.sleep(API.mediumRandom());
                    }
                }
            }
        }
    }

    public static boolean playerIsAt(Area areaYouNeedToBe) {
        return areaYouNeedToBe.contains(Players.getLocal());
    }

    public static void runFromAttacker() {
        Player local = Players.getLocal();
        if (local.getTarget() != null) {
            String attacker = local.getTarget().getName();
            Log.info("I am being attacked by" + " " + attacker);
            if (!Movement.isRunEnabled()) {
                if (Movement.getRunEnergy() >= 1) {
                    Log.info("Toggeling run to run away from my attacker");
                    Movement.toggleRun(true);
                    Time.sleepUntil(() -> Movement.isRunEnabled(), 2000);
                }
            }
        }
    }

    public static void interactWithSceneobject(String sceneObjectName, String action, Position sceneObjectPosition) {
        SceneObject sceneObject = SceneObjects.getNearest(sceneObjectName);
        if (!sceneObjectPosition.isPositionInteractable() || sceneObjectPosition.distance() > 10) {
            Log.info("Walking to" + " " + sceneObjectName);
            Movement.walkTo(sceneObjectPosition, MovementBreaks::shouldBreakOnTarget);
        }
        if (sceneObjectPosition.isPositionInteractable()) {
            if (sceneObject != null) {
                if (sceneObject.containsAction(action)) {
                    Log.info("Interacting with" + " " + sceneObjectName);
                    sceneObject.interact(action);
                    Time.sleepUntil(() -> !sceneObject.containsAction(action), API.mediumRandom());
                    if (Dialog.isOpen()) {
                        Dialog.process();
                    }
                }
            }
        }
    }

    public static void withdrawItem(boolean stack, String itemName, int amount) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        if (Bank.isOpen()) {
            int amountInInventory = Inventory.getCount(stack, x -> x.getName().contains(itemName));
            if (amountInInventory < amount) {
                if (Bank.withdraw(x -> x.getName().contains(itemName), amount - amountInInventory)) {
                    Time.sleepUntilForDuration(() -> Inventory.getCount(stack, x -> x.getName().contains(itemName)) == amount, Random.nextInt(600, 800), Random.mid(8000, 10000));
                }
            }
        }
    }

    public static void interactWithSceneobject(int sceneObjectID, String action, Position sceneObjectPosition) {
        SceneObject sceneObject = SceneObjects.getNearest(sceneObjectID);
        if (!sceneObjectPosition.isPositionInteractable() || sceneObjectPosition.distance() > 10) {
            Log.info("Walking to" + " " + sceneObjectID);
            Movement.walkTo(sceneObjectPosition, MovementBreaks::shouldBreakOnTarget);
        }
        if (sceneObjectPosition.isPositionInteractable()) {
            if (sceneObject != null) {
                Log.info("The sceneobject is there");
                if (sceneObject.containsAction(action)) {
                    Log.info("Interacting with" + " " + sceneObjectID);
                    sceneObject.interact(action);
                    Time.sleepUntil(() -> !sceneObject.containsAction(action), API.mediumRandom());
                    if (Dialog.isOpen()) {
                        Dialog.process();
                    }
                }
            }
        }
    }

    public static boolean isOnFloorLevel(int floorLevel) {
        Player local = Players.getLocal();
        return local.getFloorLevel() == floorLevel;
    }

    public static void interactWithSceneobjectWithoutMoving(int sceneObjectID, String action) {
        SceneObject sceneObject = SceneObjects.getNearest(sceneObjectID);
        if (sceneObject != null) {
            if (sceneObject.containsAction(action)) {
                Log.info("Interacting with" + " " + sceneObjectID);
                sceneObject.interact(action);
                Time.sleepUntil(() -> !sceneObject.containsAction(action), API.mediumRandom());
                if (Dialog.isOpen()) {
                    Dialog.process();
                }
            }
        }

    }

    public static void interactWithSceneobjectWithoutMoving(String sceneObjectName, String action) {
        SceneObject sceneObject = SceneObjects.getNearest(sceneObjectName);
        if (sceneObject != null) {
            if (sceneObject.containsAction(action)) {
                Log.info("Interacting with" + " " + sceneObjectName);
                sceneObject.interact(action);
                Time.sleepUntil(() -> !sceneObject.containsAction(action), API.mediumRandom());
                if (Dialog.isOpen()) {
                    Dialog.process();
                }
            }
        }
    }

    public static void doEating(int hpToEatAt) {
        if (Health.getCurrent() <= hpToEatAt) {
            Item food = Inventory.getFirst(x -> x.containsAction(EAT_ACTION));
            if (food != null) {
                int healthBefore = Health.getCurrent();
                food.interact(EAT_ACTION);
                Time.sleepUntil(() -> Health.getCurrent() != healthBefore, 5000);
            }
        }
    }

    public static void interactWithItemInInventory(String itemName, String action) {
        Item item = Inventory.getFirst(x -> x.getName().contains(itemName));
        if (item != null) {
            if (item.containsAction(action)) {
                Log.info("Interacting with" + " " + itemName);
                item.interact(action);
                Time.sleepUntil(() -> !item.containsAction(action), API.mediumRandom());
                if (Dialog.isOpen()) {
                    Dialog.process();
                }
            }
        }
        if (item == null) {
            Log.info("I don't have" + " " + itemName);
        }
    }

    public static void interactWithItemInInventory(int itemID, String action) {
        Item item = Inventory.getFirst(x -> x.getId() == itemID);
        if (item != null) {
            if (item.containsAction(action)) {
                Log.info("Interacting with" + " " + itemID);
                item.interact(action);
                Time.sleepUntil(() -> !item.containsAction(action), API.mediumRandom());
                if (Dialog.isOpen()) {
                    Dialog.process();
                }
            }
        }
        if (item == null) {
            Log.info("I don't have" + " " + itemID);
        }
    }

    public static void wearItem(String itemName) {
        Item item = Inventory.getFirst(x -> x.getName().contains(itemName));
        if (!Equipment.contains(x -> x.getName().contains(itemName))) {
            if (Inventory.contains(x -> x.getName().contains(itemName))) {
                if (item.containsAction(WEAR_ACTION)) {
                    Log.info("I am wearing" + " " + itemName);
                    item.interact(WEAR_ACTION);
                    Time.sleepUntil(() -> Equipment.contains(x -> x.getName().contains(itemName)), 5000);
                }
                if (item.containsAction(WIELD_ACTION)) {
                    Log.info("I am wielding" + " " + itemName);
                    item.interact(WIELD_ACTION);
                    Time.sleepUntil(() -> Equipment.contains(x -> x.getName().contains(itemName)), 5000);
                }
            }
        }
    }

    public static boolean isWearingItem(String itemName) {
        Item item = Inventory.getFirst(x -> x.getName().contains(itemName));
        return Equipment.contains(x -> x.getName().contains(itemName));
    }

    public static boolean inventoryHasItem(boolean stack, String itemName, int amount) {
        Item item = Inventory.getFirst(x -> x.getName().contains(itemName));
        return Inventory.getCount(stack, x -> x.getName().contains(itemName)) == amount;
    }

    public static boolean inventoryHasItem(boolean stack, int itemID, int amount) {
        Item item = Inventory.getFirst(x -> x.getId() == itemID);
        return Inventory.getCount(stack, x -> x.getId() == itemID) == amount;
    }

    public static void talkTo(String npcName, Position npcPosition) {
        Npc npc = Npcs.getNearest(npcName);
        if (!Dialog.isOpen()) {
            if (!npcPosition.isPositionInteractable() || npcPosition.distance() > 10) {
                Log.info("I am walking to" + " " + npcName);
                Movement.walkTo(npcPosition);
            }
            if (npcPosition.isPositionInteractable()) {
                if (npc != null) {
                    Log.info("Talking to" + " " + npcName);
                    npc.interact(TALK_TO_ACTION);
                    Time.sleepUntil(() -> Dialog.isOpen(), 10_000);
                }
                if (!npcPosition.isPositionInteractable() || npcPosition.distance() > 10) {
                    Log.info("I am walking to" + " " + npcName);
                    Movement.walkTo(npcPosition, MovementBreaks::shouldBreakOnRunenergy);
                }
            }
        }
    }

    public static void doDialog() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Log.info("Continuing chat");
                Dialog.processContinue();
            }
        }
    }

    public static void doDialogOption(String textOfChatOption) {
        if (Dialog.isOpen()) {
            if (Dialog.getChatOptions().equals(textOfChatOption)) {
                Log.info("Handeling chat option" + " " + textOfChatOption);
                Dialog.process(textOfChatOption);
            }
        }
    }

    public static void toggleRun() {
        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Log.info("Toggling run");
                Movement.toggleRun(true);
                Time.sleepUntil(() -> Movement.isRunEnabled(), 2000);
            }
        }
    }

    public static void getGoldFromMule(String muleName, int muleWorld, Position mulePosition, int muleAmount) {
        String COINS = "Coins";
        String TRADE_ACTION = "Trade with";
        if (Worlds.getCurrent() != muleWorld) {
            Log.info("I am hopping to the mule world");
            if (WorldHopper.hopTo(muleWorld)) {
                Time.sleepUntil(() -> !Game.isLoggedIn(), API.highRandom());
                Time.sleepUntil(Game::isLoggedIn, API.highRandom());
            }
        }

        if (Worlds.getCurrent() == muleWorld) {
            if (mulePosition.distance() > 15) {
                Log.info("I am walking to the mule");
                Movement.walkTo(mulePosition, MovementBreaks::shouldBreakOnTarget);
            }

            if (mulePosition.distance() <= 15) {
                if (!Inventory.contains(COINS)) {
                    Player mule = Players.getNearest(muleName);
                    if (mule != null) {
                        if (!Trade.isOpen()) {
                            Log.info("I am offering the mule to trade");
                            if (mule.interact(TRADE_ACTION)) {
                                Time.sleepUntil(Trade::isOpen, API.highRandom());
                            }
                        }
                        if (Trade.isOpen()) {
                            if (Trade.isOpen(false)) {
                                Log.info("The first trade screen is open");
                                if (Trade.hasOtherAccepted()) {
                                    Log.info("I am accepting first trade screen");
                                    if (Trade.accept()) {
                                        Time.sleepUntil(() -> Trade.isOpen(true), API.highRandom());
                                    }
                                }
                            }
                            if (Trade.isOpen(true)) {
                                Log.info("The second trade screen is open");
                                if (Trade.hasOtherAccepted()) {
                                    Log.info("I am accepting second trade screen");
                                    if (Trade.accept()) {
                                        Time.sleepUntil(() -> Inventory.contains(COINS), API.highRandom());
                                    }
                                }
                            }
                        }
                    }
                    if (mule == null) {
                        Log.info("I can't find the mule, I'll wait for him");
                        Time.sleepUntil(() -> Players.getNearest(muleName) != null, API.highRandom());
                    }
                }
                if (Inventory.contains(COINS)) {
                    if (Inventory.getCount(true, COINS) >= muleAmount) {
                        Log.info("I did receive enough starters gold");
                    }
                    if (Inventory.getCount(true, COINS) < muleAmount) {
                        Log.info("I didn't receive enough starters gold");
                    }
                }
            }
        }
    }

    public static int lowRandom() {
        return Random.mid(299, 444);
    }

    public static int mediumRandom() {
        return Random.mid(1499, 2222);
    }

    public static int highRandom() {
        return Random.mid(5555, 7777);
    }

}
