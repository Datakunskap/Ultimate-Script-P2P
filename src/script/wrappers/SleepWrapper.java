package script.wrappers;

import org.rspeer.runetek.api.commons.math.Random;

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

}
