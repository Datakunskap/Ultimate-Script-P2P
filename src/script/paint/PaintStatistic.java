package script.paint;

import org.rspeer.runetek.api.commons.StopWatch;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class PaintStatistic {

    private final boolean heading;
    private final Supplier<String> supplier;

    public PaintStatistic(boolean heading, Supplier<String> supplier) {
        this.heading = heading;
        this.supplier = supplier;
    }

    public PaintStatistic(Supplier<String> supplier) {
        this(false, supplier);
    }

    public PaintStatistic(StopWatch runtime, IntSupplier rate) {
        this(false, () -> {
            int value = rate.getAsInt();
            return NumericFormat.apply(value) + " (" + NumericFormat.apply((long) runtime.getHourlyRate(value)) + " / hr)";
        });
    }

    public boolean isHeading() {
        return heading;
    }

    public String toString() {
        return supplier.get();
    }
}