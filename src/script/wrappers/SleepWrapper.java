package script.wrappers;

import org.rspeer.runetek.api.commons.math.Random;

public class SleepWrapper {

    public static int shortSleep350() {
        return Random.mid(299,399);
    }

    public static int shortSleep600() {
        return Random.mid(399, 799);
    }

    public static int mediumSleep1500() {
        return Random.mid(999,1999);
    }

    public static int mediumSleep1000() {
        return Random.mid(500,1500);
    }

    public static int longSleep7500() {
        return Random.mid(4999,9999);
    }

}
