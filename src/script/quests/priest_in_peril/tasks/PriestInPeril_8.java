package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import java.util.function.Predicate;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_8 extends Task {

    private static final String STAMINA_POTION = "Stamina potion(";
    private static final String RUNE_ESSENCE = "Rune essence";
    private static final Predicate<String> YES = o -> o.contains("Yes");

    private static final Position DREZEL_POSITION = new Position(3439, 9897, 0);

    private static final Area INSIDE_TEMPLE = Area.rectangular(3409, 3494, 3418, 3482);
    private static final Area DOWNSTAIRS = Area.rectangular(3399, 9911, 3446, 9876);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 8;
    }

    @Override
    public int execute() {
        Player local = Players.getLocal();

        if (Dialog.canContinue()) {
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Movement.toggleRun(true);
            }
        }

        if (!Movement.isStaminaEnhancementActive()) {
            if (Inventory.contains(x -> x.getName().contains(STAMINA_POTION))) {
                Item staminaPotion = Inventory.getFirst(x -> x.getName().contains(STAMINA_POTION));
                if (staminaPotion.interact("Drink")) {
                    Time.sleepUntil(() -> Movement.isStaminaEnhancementActive(), 5000);
                }
            }
        }

        if (Inventory.contains(RUNE_ESSENCE)) {
            if (local.getFloorLevel() == 2) {
                SceneObject ladder = SceneObjects.getNearest(16679);
                if (ladder != null) {
                    if (!ladder.isPositionInteractable()) {
                        SceneObject jailDoor = SceneObjects.getNearest(3463);
                        if (jailDoor != null) {
                            if (jailDoor.interact("Open")) {
                                Time.sleepUntil(() -> ladder.isPositionInteractable(), 5000);
                            }
                        }
                    }
                    if (ladder.isPositionInteractable()) {
                        if (ladder.interact("Climb-down")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 1, 10000);
                        }
                    }
                }
            }
            if (local.getFloorLevel() == 1) {
                SceneObject staircase = SceneObjects.getNearest(16673);
                if (staircase != null) {
                    if (staircase.isPositionInteractable()) {
                        if (staircase.interact("Climb-down")) {
                            Time.sleepUntil(() -> local.getFloorLevel() == 0, 10000);
                        }
                    }
                }
            }
            if (local.getFloorLevel() == 0) {
                SceneObject trapDoorClosed = SceneObjects.getNearest(1579);
                SceneObject trapDoorOpen = SceneObjects.getNearest(1581);
                if (INSIDE_TEMPLE.contains(local)) {
                    if(trapDoorClosed != null){
                        Movement.walkToRandomized(trapDoorClosed);
                    }
                    if(trapDoorOpen != null){
                        Movement.walkToRandomized(trapDoorOpen);
                    }
                }
                if (!INSIDE_TEMPLE.contains(local)) {
                    if (trapDoorClosed != null) {
                        if (trapDoorClosed.containsAction("Open")) {
                            if (trapDoorClosed.interact("Open")) {
                                Time.sleepUntil(() -> trapDoorOpen.containsAction("Climb-down"), 10000);
                            }
                        }
                    }
                    if (trapDoorOpen != null) {
                        if (trapDoorOpen.containsAction("Climb-down")) {
                            if (!Dialog.isOpen()) {
                                if (trapDoorOpen.interact("Climb-down")) {
                                    Time.sleepUntil(() -> Dialog.isOpen(), 10000);
                                }
                            }
                            if (Dialog.isOpen()) {
                                if (Dialog.canContinue()) {
                                    Dialog.processContinue();
                                }
                                if (Dialog.getChatOption(YES).isVisible()) {
                                    Dialog.process(YES);
                                }
                            }
                        }
                    }
                }
            }
            if(DOWNSTAIRS.contains(local)){
                Npc drezel = Npcs.getNearest("Drezel");
                if(drezel == null){
                    Movement.walkToRandomized(DREZEL_POSITION);
                }
                if(drezel != null){
                    if(!drezel.isPositionInteractable()){
                        Movement.walkToRandomized(drezel);
                    }
                    if(drezel.isPositionInteractable()){
                        if(!Dialog.isOpen()){
                            if(drezel.interact("Talk-to")){
                                Time.sleepUntil(()-> Dialog.isOpen(), 10000);
                            }
                        }
                        if(Dialog.isOpen()){
                            if(Dialog.canContinue()){
                                Dialog.processContinue();
                            }
                        }
                    }
                }
            }
        }

        return lowRandom();
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}
