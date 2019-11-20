package script.quests.witches_house.tasks;

import api.API;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.ITEM_ON_OBJECT;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_3 extends Task {

    private static final int DOOR_TO_GARDEN = 2862;

    private static final String OPEN_ACTION = "Open";

    private static final Position SAFE_SPOT = new Position(2936, 3459);
    private static final Position POSITION_ONE = new Position(2908, 3460);
    private static final Position POSITION_TWO = new Position(2916, 3460);
    private static final Position POSITION_THREE = new Position(2924, 3460);
    private static final Position POSITION_FOUR = new Position(2933, 3463);
    private static final Position POSITION_FIVE = new Position(2927, 3466);
    private static final Position POSITION_SIX = new Position(2920, 3466);
    private static final Position POSITION_SEVEN = new Position(2913, 3466);

    private static final Area GARDEN_ONE = Area.rectangular(2900, 3465, 2937, 3459);
    private static final Area GARDEN_TWO = Area.rectangular(2908, 3477, 2937, 3458);
    private static final Area SHED = Area.rectangular(2934, 3467, 2937, 3459);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 3;
    }

    @Override
    public int execute() {

        Log.info("WitchesHouse_3");

        API.doDialog();

        API.toggleRun();

        Npc Witch = Npcs.getNearest(3995);
        if (!GARDEN_ONE.contains(Players.getLocal()) && !GARDEN_TWO.contains(Players.getLocal())) {
            API.interactWithSceneobjectWithoutMoving(DOOR_TO_GARDEN, OPEN_ACTION);
        }
        if (Witch != null) {
            if (!Inventory.contains(2411) && !SHED.contains(Players.getLocal())) {
                if (Players.getLocal().getY() < 3466) {
                    if (Witch.getOrientation() == 1536) {
                        if (Witch.getX() > 2907) {
                            if (Players.getLocal().getX() < 2908) {
                                Log.info("Moving to pos 2");
                                Movement.setWalkFlag(POSITION_ONE);
                            }
                        }
                        if (Witch.getX() > 2915) {
                            if (POSITION_ONE.distance() < 1) {
                                Movement.setWalkFlag(POSITION_TWO);
                                Log.info("Moving to pos 3");
                            }
                        }
                        if (Witch.getX() > 2922) {
                            if (POSITION_TWO.distance() < 1) {
                                Movement.setWalkFlag(POSITION_THREE);
                                Log.info("Moving to pos 4");
                            }
                        }
                    }
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2925) {
                            if (POSITION_THREE.distance() < 1) {
                                Movement.setWalkFlag(POSITION_FOUR);
                                Log.info("Moving to pos 5");
                            }
                        }
                    }
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2928) {
                            if (POSITION_FOUR.distance() < 1) {
                                Movement.setWalkFlag(POSITION_FIVE);
                                Log.info("Moving to pos 6");
                            }
                        }
                    }
                }
                if (Players.getLocal().getY() == 3466) {
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2921) {
                            if (POSITION_FIVE.distance() < 1) {
                                Movement.setWalkFlag(POSITION_SIX);
                                Log.info("Moving to pos 7");
                            }
                        }
                    }
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2914) {
                            if (POSITION_SIX.distance() < 1) {
                                Movement.setWalkFlag(POSITION_SEVEN);
                                Log.info("Moving to pos 8");
                            }
                        }
                    }
                }
                if (Players.getLocal().getX() <= 2913 && Players.getLocal().getY() >= 3466) {
                    if (SceneObjects.getNearest(2864) != null && !Dialog.isOpen()) {
                        SceneObjects.getNearest(2864).click();
                        API.lowRandom();
                        API.lowRandom();
                    }
                    if (Dialog.isOpen()) {
                        Dialog.processContinue();
                        API.lowRandom();
                    }
                }
            }
            if (Inventory.contains(2411)) {
                if (Witch.getOrientation() == 512) {
                    if (Witch.getX() < 2914) {
                        if (POSITION_FOUR.distance() > 1) {
                            Movement.setWalkFlag(POSITION_FOUR);
                        }
                    }
                }
                //add in world hopping if someone else is at the shed
                if (POSITION_FOUR.distance() < 1 && !SHED.contains(Players.getLocal())) {
                    Inventory.getFirst(2411).interact("Use");
                    API.lowRandom();
                    SceneObjects.getNearest(2863).interact(ITEM_ON_OBJECT);
                    API.lowRandom();
                }
            }
        }
        if (SHED.contains(Players.getLocal())) {
            if (Skills.getCurrentLevel(Skill.HITPOINTS) < Skills.getLevel(Skill.HITPOINTS) - 5) {
                if (Inventory.contains("Monkfish")) {
                    Inventory.getFirst("Monkfish").interact("Eat");
                    API.lowRandom();
                    API.lowRandom();
                }
            }
            if (!Magic.Autocast.isEnabled()) {
                if (Skills.getLevel(Skill.MAGIC) >= 13) {
                    if (Magic.Autocast.getSelectedSpell() != Spell.Modern.FIRE_STRIKE) {
                        Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.FIRE_STRIKE);
                    }
                }
            }
            if (Magic.Autocast.isEnabled()) {
                if (Skills.getLevel(Skill.MAGIC) >= 13) {
                    if (Magic.Autocast.getSelectedSpell() != Spell.Modern.FIRE_STRIKE) {
                        Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.FIRE_STRIKE);
                    }
                }
                Npc Experiment = Npcs.getNearest(x -> x.getName().contains("Witch"));
                if (Experiment == null) {
                    if (Pickables.getNearest("Ball") != null) {
                        Pickables.getNearest("Ball").click();
                        API.lowRandom();
                    }
                }
                if (Experiment != null) {
                    if (Dialog.isOpen()) {
                        Dialog.processContinue();
                        API.lowRandom();
                    }
                    if (!Combat.isAutoRetaliateOn()) {
                        Combat.toggleAutoRetaliate(true);
                        API.lowRandom();
                    }
                    if (SAFE_SPOT.distance() > 0) {
                        Movement.setWalkFlag(SAFE_SPOT);
                        API.lowRandom();
                    }
                    if (SAFE_SPOT.distance() < 1) {
                        if (Players.getLocal().getTargetIndex() == -1) {
                            Experiment.interact("Attack");
                            Time.sleepUntil(() -> Players.getLocal().getTargetIndex() != -1, 5000);
                        }
                    }
                }
            }
        }
        return API.lowRandom();
    }

}