package script.quests.the_restless_ghost.data;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;

public enum Quest {

    WATERFALL(65),
    THE_RESTLESS_GHOST(107),
    WITCHES_HOUSE(226),
    PRIEST_IN_PERIL(302),
    NATURE_SPIRIT(307);

    private final int varpId;

    Quest(int varpId) {
        this.varpId = varpId;
    }

    public int getVarpId() {
        return varpId;
    }

    public int getVarpValue() {
        return Game.isLoggedIn() ? Varps.get(varpId) : -1;
    }

    @Override
    public String toString() {
        String fixed = name().toLowerCase().replace("_", " ");
        return fixed.substring(0, 1).toUpperCase().concat(fixed.substring(1));
    }
}