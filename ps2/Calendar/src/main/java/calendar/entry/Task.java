package calendar.entry;

import java.time.LocalDate;
import java.time.LocalTime;

import static controllers.CalendarController.timeFormatter;

public class Task extends Event {
    private LocalTime startTime;
    private LocalTime endTime;

    public Task(String title, LocalDate startDate, LocalTime startTime, LocalTime endTime) {
        super(title, startDate);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(String title, LocalDate startDate, LocalDate endDate, RepeatPeriod repeatPeriod,
            LocalTime startTime, LocalTime endTime) {
        super(title, startDate, endDate, repeatPeriod);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(String title, LocalDate startDate, int repeatNumber, RepeatPeriod repeatPeriod,
            LocalTime startTime, LocalTime endTime) {
        super(title, startDate, repeatNumber, repeatPeriod);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(Event event, LocalTime startTime, LocalTime endTime) {
        super(event.title, event.meetingLink, event.dateList);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Task: " + title + " " + getMeetingMarkerString()
                + " " + timeFormatter.format(startTime)
                + " " + timeFormatter.format(endTime);
    }
}
