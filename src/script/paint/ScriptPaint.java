package script.paint;

import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import script.Main;
import script.wrappers.BankWrapper;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;

public final class ScriptPaint implements RenderListener {

    private static final int BASE_X = 6;
    private static final int BASE_Y = 6;

    private static final int DEFAULT_WIDTH_INCR = 20;

    private static final int BASE_HEIGHT = 20;
    private static final int LINE_HEIGHT = 20;

    private static final Color FOREGROUND = Color.WHITE;
    private static final Color BACKGROUND = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke(1.8f);
    private final DecimalFormat formatNumber = new DecimalFormat("#,###");

    private final Map<String, PaintStatistic> stats;

    private Color outline;
    private Main context;

    public ScriptPaint(Main context) {
        this.context = context;
        stats = new LinkedHashMap<>();
        outline = new Color(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255));

        stats.put("SS Fungi", new PaintStatistic(true, () -> " by " + "Streagrem & DrScatman"));
        stats.put("Runtime", new PaintStatistic(() -> context.getRuntime().toElapsedString()));
        stats.put("Task", new PaintStatistic(() -> context.getCurrent() != null ? context.getCurrent().getClass().getSimpleName() : "None"));
        stats.put("Inventory Value", new PaintStatistic(()
                -> formatNumber.format(BankWrapper.getInventoryValue())));
        stats.put("Bank Value", new PaintStatistic(()
                -> formatNumber.format(BankWrapper.getBankValue())));
        stats.put("Total Value", new PaintStatistic(()
                -> formatNumber.format(BankWrapper.getTotalValue())));
        stats.put("Value Gained", new PaintStatistic(()
                -> formatNumber.format(BankWrapper.getTotalValueGained())));
        stats.put("Value / H", new PaintStatistic(()
                -> format((long) context.getRuntime().getHourlyRate(BankWrapper.getTotalValueGained()))));
        stats.put("Amount Muled", new PaintStatistic(()
                -> formatNumber.format(BankWrapper.getAmountMuled())));
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    private String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public Color getOutline() {
        return outline;
    }

    public void setOutline(Color outline) {
        this.outline = outline;
    }

    public void submit(String key, PaintStatistic tracker) {
        stats.put(key, tracker);
    }

    private Duration duration = Duration.ofSeconds(5);

    @Override
    public void notify(RenderEvent e) {
        if (context.getRuntime().exceeds(duration)) {
            outline = new Color(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255));
            duration = context.getRuntime().getElapsed().plus(duration);
        }
        Graphics2D g = (Graphics2D) e.getSource();
        Composite defaultComposite = g.getComposite();

        int width = 180;
        int currentX = BASE_X + (DEFAULT_WIDTH_INCR / 2);
        int currentY = BASE_Y + (LINE_HEIGHT / 2);

        g.setStroke(STROKE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(FOREGROUND);

        for (Map.Entry<String, PaintStatistic> entry : stats.entrySet()) {
            PaintStatistic stat = entry.getValue();
            String string = entry.getKey() + (stat.isHeading() ? " - " : ": ") + stat.toString();
            int currentWidth = g.getFontMetrics().stringWidth(string);
            if (currentWidth > width) {
                width = currentWidth;
            }
        }

        g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        g.setColor(BACKGROUND);
        g.fillRoundRect(BASE_X, BASE_Y, width + DEFAULT_WIDTH_INCR, (stats.size() * LINE_HEIGHT) + BASE_HEIGHT, 7, 7);

        g.setComposite(defaultComposite);
        g.setColor(outline);
        g.drawRoundRect(BASE_X, BASE_Y, width + DEFAULT_WIDTH_INCR, (stats.size() * LINE_HEIGHT) + BASE_HEIGHT, 7, 7);

        g.setColor(FOREGROUND);
        for (Map.Entry<String, PaintStatistic> entry : stats.entrySet()) {
            PaintStatistic stat = entry.getValue();

            String string = entry.getKey() + (stat.isHeading() ? " - " : ": ") + stat.toString();
            int drawX = currentX;
            if (stat.isHeading()) {
                drawX = BASE_X + ((width + DEFAULT_WIDTH_INCR) - g.getFontMetrics().stringWidth(string)) / 2;
                g.setColor(outline);
                g.drawRect(BASE_X, currentY + (LINE_HEIGHT / 2) - BASE_Y + 1, width + DEFAULT_WIDTH_INCR, LINE_HEIGHT);

                g.setComposite(AlphaComposite.SrcOver.derive(0.1f));
                g.fillRect(BASE_X, currentY + (LINE_HEIGHT / 2) - BASE_Y + 1, width + DEFAULT_WIDTH_INCR, LINE_HEIGHT);
                g.setComposite(defaultComposite);

                g.setFont(g.getFont().deriveFont(Font.BOLD));
            } else {
                g.setFont(g.getFont().deriveFont(Font.PLAIN));
            }

            g.setColor(FOREGROUND);
            g.drawString(string, drawX, currentY += LINE_HEIGHT);
        }
    }
}
