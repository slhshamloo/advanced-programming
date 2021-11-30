package calendar.entry;

public enum RepeatPeriod {
    DAY("D"),
    WEEK("W"),
    MONTH("M");

    private final String marker;

    RepeatPeriod(String marker) {
        this.marker = marker;
    }

    public static RepeatPeriod getRepeatPeriodByMarker(String marker) {
        for (RepeatPeriod repeatPeriod : RepeatPeriod.values())
            if (repeatPeriod.marker.equals(marker))
                return repeatPeriod;
        return null;
    }
}
