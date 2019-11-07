package script.tasks.fungus.data;

import org.rspeer.runetek.adapter.component.Item;

import java.util.function.Predicate;

public class Items {

    public static Predicate<Item> MORT_MYRE_FUNGUS = x -> x.getName().contains("Mort myre fungus");
    public static Predicate<Item> SALVE_GRAVEYARD_TELEPORT = x -> x.getName().contains("Salve graveyard teleport");
    public static Predicate<Item> RING_OF_DUELING = x -> x.getName().contains("Ring of dueling(");
    public static Predicate<Item> SILVER_SICKLE = x -> x.getName().contains("Silver sickle (b)");

}
