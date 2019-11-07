package script.quests.witches_house.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
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

    private static final Position Pot = new Position(2899, 3473);
    private static final Position Ladder = new Position(2906, 3476);

    private Area Garden = Area.rectangular(2900, 3465, 2937, 3459);
    private Area Garden2 = Area.rectangular(2908, 3477, 2937, 3458);
    private Area Shed = Area.rectangular(2934, 3467, 2937, 3459);
    private Position SafeSpot = new Position(2936, 3459);
    private Position Pos2 = new Position(2908, 3460);
    private Position Pos3 = new Position(2916, 3460);
    private Position Pos4 = new Position(2924, 3460);
    private Position Pos5 = new Position(2933, 3463);
    private Position Pos6 = new Position(2927, 3466);
    private Position Pos7 = new Position(2920, 3466);
    private Position Pos8 = new Position(2913, 3466);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 3;
    }

    @Override
    public int execute() {
        Npc Witch = Npcs.getNearest(3995);
        if (!Garden.contains(Players.getLocal()) && !Garden2.contains(Players.getLocal())) {
            interactWithObject(2862);
            RandomSleep();
        }
        if (Witch != null) {
            if (!Inventory.contains(2411) && !Shed.contains(Players.getLocal())) {
                if (Players.getLocal().getY() < 3466) {
                    if (Witch.getOrientation() == 1536) {
                        if (Witch.getX() > 2907) {
                            if (Players.getLocal().getX() < 2908) {
                                Log.info("Moving to pos 2");
                                Movement.setWalkFlag(Pos2);
                            }
                        }
                        if (Witch.getX() > 2915) {
                            if (Pos2.distance() < 1) {
                                Movement.setWalkFlag(Pos3);
                                Log.info("Moving to pos 3");
                            }
                        }
                        if (Witch.getX() > 2922) {
                            if (Pos3.distance() < 1) {
                                Movement.setWalkFlag(Pos4);
                                Log.info("Moving to pos 4");
                            }
                        }
                    }
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2925) {
                            if (Pos4.distance() < 1) {
                                Movement.setWalkFlag(Pos5);
                                Log.info("Moving to pos 5");
                            }
                        }
                    }
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2928) {
                            if (Pos5.distance() < 1) {
                                Movement.setWalkFlag(Pos6);
                                Log.info("Moving to pos 6");
                            }
                        }
                    }
                }
                if (Players.getLocal().getY() == 3466) {
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2921) {
                            if (Pos6.distance() < 1) {
                                Movement.setWalkFlag(Pos7);
                                Log.info("Moving to pos 7");
                            }
                        }
                    }
                    if (Witch.getOrientation() == 512) {
                        if (Witch.getX() < 2914) {
                            if (Pos7.distance() < 1) {
                                Movement.setWalkFlag(Pos8);
                                Log.info("Moving to pos 8");
                            }
                        }
                    }
                }
                if (Players.getLocal().getX() <= 2913 && Players.getLocal().getY() >= 3466) {
                    if (SceneObjects.getNearest(2864) != null && !Dialog.isOpen()) {
                        SceneObjects.getNearest(2864).click();
                        RandomSleep();
                        RandomSleep();
                    }
                    if (Dialog.isOpen()) {
                        Dialog.processContinue();
                        RandomSleep();
                    }
                }
            }
            if (Inventory.contains(2411)) {
                if (Witch.getOrientation() == 512) {
                    if (Witch.getX() < 2914) {
                        if (Pos5.distance() > 1) {
                            Movement.setWalkFlag(Pos5);
                        }
                    }
                }
                if (Pos5.distance() < 1 && !Shed.contains(Players.getLocal())) {
                    Inventory.getFirst(2411).interact("Use");
                    RandomSleep();
                    SceneObjects.getNearest(2863).interact(ITEM_ON_OBJECT);
                    RandomSleep();
                }
            }
        }
        if (Shed.contains(Players.getLocal())) {
            if (Skills.getCurrentLevel(Skill.HITPOINTS) < Skills.getLevel(Skill.HITPOINTS) - 5) {
                if (Inventory.contains("Tuna")) {
                    Inventory.getFirst("Tuna").interact("Eat");
                    RandomSleep();
                    RandomSleep();
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
                        RandomSleep();
                    }
                }
                if (Experiment != null) {
                    if (Dialog.isOpen()) {
                        Dialog.processContinue();
                        RandomSleep();
                    }
                    if (!Combat.isAutoRetaliateOn()) {
                        Combat.toggleAutoRetaliate(true);
                        RandomSleep();
                    }
                    if (SafeSpot.distance() > 0) {
                        Movement.setWalkFlag(SafeSpot);
                        RandomSleep();
                    }
                    if (SafeSpot.distance() < 1) {
                        if (Players.getLocal().getTargetIndex() == -1) {
                            Experiment.interact("Attack");
                            Time.sleepUntil(() -> Players.getLocal().getTargetIndex() != -1, 5000);
                        }
                    }
                }
            }
        }
        return 600;
    }

    public void interactWithObject(int ID) {
        if (SceneObjects.getNearest(ID) != null) {
            SceneObjects.getNearest(ID).click();
            RandomSleep();
        }
    }

    public void clickDialogComponenet(int Option) {
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Interfaces.getComponent(219, 1, Option).click();
            RandomSleep();
        }
    }

    public String getComponentOptions(int Option) {
        String Text = "Null";
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Text = Interfaces.getComponent(219, 1, Option).getText();
        }
        return Text;
    }

    public String getDialogOptions() {
        String Text = "Null";
        if (Interfaces.getComponent(219, 1, 0) != null) {
            Text = Interfaces.getComponent(219, 1, 0).getText();
        }
        return Text;
    }

    public String getDialog() {
        String Text = "Null";
        if (Interfaces.getComponent(263, 1, 0) != null) {
            Text = Interfaces.getComponent(263, 1, 0).getText();
        }
        return Text;
    }

    public void RandomSleep() {
        Time.sleep(Random.nextInt(250, 550));
    }
}