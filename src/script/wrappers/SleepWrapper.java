package script.wrappers;

import org.rspeer.runetek.api.commons.math.Random;

import java.util.HashMap;

public class SleepWrapper {

    public static int shortSleep350() {
        return Random.mid(299,399);
    }

    public static int mediumSleep1500() {
        return Random.mid(999,1999);
    }

    public static int longSleep7500() {
        return Random.mid(4999,9999);
    }

    public static void mapTutorial() {
        HashMap<String, Integer> map = new HashMap();
        map.put("Staff of fire", 1);
        int quantity = map.get("Staff of fire");

    }
}
