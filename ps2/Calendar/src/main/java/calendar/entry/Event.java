package calendar.entry;

import calendar.MahmoudiCalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event {
    protected final List<LocalDate> dateList;
    protected String title;
    protected String meetingLink = null;
    private RepeatPeriod repeatPeriod = RepeatPeriod.DAY;
    private boolean hasEndDate = false;

    public Event(String title, LocalDate startDate) {
        this.title = title;
        dateList = new ArrayList<>();
        dateList.add(startDate);
    }

    public Event(String title, LocalDate startDate, LocalDate endDate, RepeatPeriod repeatPeriod) {
        this(title, startDate);
        this.repeatPeriod = repeatPeriod;

        setDatesByEndDate(endDate);
        hasEndDate = true;
    }

    public Event(String title, LocalDate startDate, long repeatNumber, RepeatPeriod repeatPeriod) {
        this(title, startDate);
        this.repeatPeriod = repeatPeriod;

        setDates(repeatNumber);
    }

    protected Event(String title, String meetingLink, List<LocalDate> dateList) {
        this.title = title;
        this.meetingLink = meetingLink;
        this.dateList = dateList;
    }

    public void setDatesByEndDate(LocalDate endDate) {
        LocalDate startDate = getStartDate();
        long repeatNumber = MahmoudiCalendar.getRepeatNumber(startDate, endDate, repeatPeriod);
        setDates(repeatNumber);
        hasEndDate = true;
    }

    public void setDatesByRepeatNumber(long repeatNumber) {
        setDates(repeatNumber);
        hasEndDate = false;
    }

    public void setDatesByRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;

        if (hasEndDate)
            setDatesByEndDate(dateList.get(dateList.size() - 1));
        else
            setDatesByRepeatNumber(dateList.size() - 1);
    }

    private void setDates(long repeatNumber) {
        LocalDate startDate = getStartDate();
        dateList.clear();

        if (repeatPeriod == RepeatPeriod.DAY)
            for (int i = 0; i <= repeatNumber; i++)
                dateList.add(startDate.plusDays(i));
        else if (repeatPeriod == RepeatPeriod.WEEK)
            for (int i = 0; i <= repeatNumber; i++)
                dateList.add(startDate.plusWeeks(i));
        else if (repeatPeriod == RepeatPeriod.MONTH)
            for (int i = 0; i <= repeatNumber; i++)
                dateList.add(startDate.plusMonths(i));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public RepeatPeriod getRepeatPeriod() {
        return repeatPeriod;
    }

    public LocalDate getStartDate() {
        return dateList.get(0);
    }

    public boolean isOnDate(LocalDate localDate) {
        return dateList.contains(localDate);
    }

    public String getMeetingMarkerString() {
        if (meetingLink == null)
            return "F";
        else
            return "T";
    }

    @Override
    public String toString() {
        return "Event: " + title + " " + getMeetingMarkerString();
    }
}
