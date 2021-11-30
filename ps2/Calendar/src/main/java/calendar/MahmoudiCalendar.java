package calendar;

import calendar.entry.Event;
import calendar.entry.RepeatPeriod;
import calendar.entry.Task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MahmoudiCalendar {
    private final Map<String, Event> events = new HashMap<>();
    private final Map<String, Task> tasks = new HashMap<>();
    private final int id;
    private String title;

    public MahmoudiCalendar(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static long getRepeatNumber(LocalDate startDate, LocalDate endDate,
            RepeatPeriod repeatPeriod) {
        if (repeatPeriod == RepeatPeriod.DAY)
            return ChronoUnit.DAYS.between(startDate, endDate);
        else if (repeatPeriod == RepeatPeriod.WEEK)
            return ChronoUnit.WEEKS.between(startDate, endDate);
        else if (repeatPeriod == RepeatPeriod.MONTH)
            return ChronoUnit.MONTHS.between(startDate, endDate);
        return -1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addEvent(Event event) {
        events.put(event.getTitle(), event);
    }

    public void addTask(Task task) {
        tasks.put(task.getTitle(), task);
    }
    
    public void editEventTitle(Event event, String newTitle) {
        String oldTitle = event.getTitle();
        events.remove(oldTitle);
        event.setTitle(newTitle);
        events.put(newTitle, event);
    }

    public void editTaskTitle(Task task, String newTitle) {
        String oldTitle = task.getTitle();
        tasks.remove(oldTitle);
        task.setTitle(newTitle);
        tasks.put(newTitle, task);
    }

    public Event getEventByTitle(String title) {
        return events.get(title);
    }

    public Task getTaskByTitle(String title) {
        return tasks.get(title);
    }

    public void removeEventByTitle(String title) {
        events.remove(title);
    }

    public void removeTaskByTitle(String title) {
        tasks.remove(title);
    }
    
    public List<Event> getEventListOnDate(LocalDate date) {
        List<Event> eventListOnDate = new ArrayList<>();
        
        for (Event event : events.values())
            if (event.isOnDate(date))
                eventListOnDate.add(event);
        
        return eventListOnDate;
    }

    public List<Task> getTaskListOnDate(LocalDate date) {
        List<Task> taskListOnDate = new ArrayList<>();

        for (Task task : tasks.values())
            if (task.isOnDate(date))
                taskListOnDate.add(task);

        return taskListOnDate;
    }

    public Event[] getSortedEvents() {
        Event[] sortedEvents = events.values().toArray(new Event[0]);
        Arrays.sort(sortedEvents, Comparator.comparing(Event::getTitle));
        return sortedEvents;
    }

    public Task[] getSortedTasks() {
        Task[] sortedTasks = tasks.values().toArray(new Task[0]);

        Arrays.sort(sortedTasks, Comparator.comparing(Task::getTitle));
        Arrays.sort(sortedTasks, Comparator.comparing(Task::getStartTime));
        Arrays.sort(sortedTasks, Comparator.comparing(Task::getStartDate));

        return sortedTasks;
    }

    @Override
    public String toString() {
        return "Calendar: " + title + " " + id;
    }
}
