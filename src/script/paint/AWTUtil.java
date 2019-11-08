package script.paint;

import java.awt.*;

public final class AWTUtil {

    public static final byte NORTH = 0x1;
    public static final byte SOUTH = 0x2;
    public static final byte EAST = 0x4;
    public static final byte WEST = 0x8;

    private AWTUtil() {
        throw new IllegalAccessError();
    }

    public static void drawBorderedRectangle(Graphics2D g, int x, int y, int width, int height, Color fg, Color bg) {
        g.setColor(bg);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);
        g.drawRect(x - 1, y - 1, width + 2, height + 2);
        g.setColor(fg);
        g.drawRect(x, y, width, height);
    }

    public static void drawShadowString(Graphics g, String string, int x, int y, Color fg, Color bg, int flags, int xOffset, int yOffset) {
        if (string == null) {
            string = "null";
        }
        g.setColor(bg);
        if ((flags & NORTH) == NORTH) {
            g.drawString(string, x, y - yOffset);
        }
        if ((flags & EAST) == EAST) {
            g.drawString(string, x + (xOffset), y);
        }
        if ((flags & SOUTH) == SOUTH) {
            g.drawString(string, x, y + yOffset);
        }
        if ((flags & WEST) == WEST) {
            g.drawString(string, x - xOffset, y);
        }
        if ((flags & NORTH) == NORTH && (flags & EAST) == EAST) {
            g.drawString(string, x + xOffset, y - yOffset);
        }
        if ((flags & SOUTH) == SOUTH && (flags & EAST) == EAST) {
            g.drawString(string, x + xOffset, y + yOffset);
        }
        if ((flags & SOUTH) == SOUTH && (flags & WEST) == WEST) {
            g.drawString(string, x - xOffset, y + yOffset);
        }
        if ((flags & NORTH) == NORTH && (flags & WEST) == WEST) {
            g.drawString(string, x - xOffset, y - yOffset);
        }
        g.setColor(fg);
        g.drawString(string, x, y);
    }

    public static void drawBoldedString(Graphics g, String string, int x, int y, Color fg, Color bg) {
        drawShadowString(g, string, x, y, fg, bg, NORTH | SOUTH | EAST | WEST, 1, 1);
    }

    public static void drawBoldedString(Graphics g, String string, int x, int y, Color color) {
        drawShadowString(g, string, x, y, color, Color.BLACK, NORTH | SOUTH | EAST | WEST, 1, 1);
    }

    public static void drawBoldedString(Graphics g, String string, int x, int y, Color color, int xOffset, int yOffset) {
        drawShadowString(g, string, x, y, color, Color.BLACK, NORTH | SOUTH | EAST | WEST, xOffset, yOffset);
    }

    public static void drawBoldedString(Graphics g, String string, int x, int y) {
        drawBoldedString(g, string, x, y, g.getColor());
    }

    public static void drawBoldedString(Graphics g, String string, int x, int y, int xOffset, int yOffset) {
        drawBoldedString(g, string, x, y, g.getColor(), xOffset, yOffset);
    }
}
