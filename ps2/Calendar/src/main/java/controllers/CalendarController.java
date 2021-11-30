package controllers;

import calendar.MahmoudiCalendar;
import calendar.entry.Event;
import calendar.entry.RepeatPeriod;
import calendar.entry.Task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarController extends AbstractController {
    public static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy_MM_dd");
    public static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH_mm");
    private final MahmoudiCalendar calendar;
    private final Boolean hasAccess;

    public CalendarController(Scanner inputStream, MahmoudiCalendar calendar, boolean hasAccess) {
        super(inputStream, "Back");

        this.calendar = calendar;
        this.hasAccess = hasAccess;
    }

    public static boolean isDateInvalid(String dateString) {
        LocalDate date;
        try {
            date = LocalDate.from(dateFormatter.parse(dateString));
        } catch (DateTimeParseException e) {
            return true;
        }
        return Integer.parseInt(dateString.substring(8)) != date.getDayOfMonth();
    }

    private static boolean isEntryTitleFormatInvalid(String title) {
        return !(title.matches("\\w+"));
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile(
                "Add Event (\\S+) (\\d{4}_\\d{2}_\\d{2}) "
                        + "(?:(?:((?:\\d+)|(?:\\d{4}_\\d{2}_\\d{2})) ([DWM]))|None) ([TF])"),
                this::addEvent);
        commandMap.put(Pattern.compile(
                "Add Task (\\S+) (\\d{2}_\\d{2}) (\\d{2}_\\d{2}) (\\d{4}_\\d{2}_\\d{2}) "
                        + "(?:(?:((?:\\d+)|(?:\\d{4}_\\d{2}_\\d{2})) ([DWM]))|None) ([TF])"),
                this::addTask);
        commandMap.put(Pattern.compile("Edit Event (\\S+) "
                        + "(?=(?:title \\S+)|(?:repeat (?:(?:\\d+)|(?:\\d{4}_\\d{2}_\\d{2})))"
                        + "|(?:kind of repeat [DWM])|(?:meet [TF]))"
                        + "(.+) (\\S+)"),
                this::editEvent);
        commandMap.put(Pattern.compile("Edit Task (\\S+) "
                        + "(?=(?:title \\S+)|(?:repeat (?:(?:\\d+)|(?:\\d{4}_\\d{2}_\\d{2})))"
                        + "|(?:kind of repeat [DWM])|(?:meet [TF])"
                        + "|(?:(?:(?:start time)|(?:end time)) \\d{2}_\\d{2}))"
                        + "(.+) (\\S+)"),
                this::editTask);
        commandMap.put(Pattern.compile("Delete Event (\\S+)"), this::removeEvent);
        commandMap.put(Pattern.compile("Delete Task (\\S+)"), this::removeTask);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("Show Events", this::printEvents);
        noArgumentCommandMap.put("Show tasks", this::printTasks);

        return noArgumentCommandMap;
    }

    @Override
    protected void escape() {
    }

    private boolean isTitleOrStartDateInvalid(String title, String startDateString) {
        if (isEntryTitleFormatInvalid(title)) {
            System.out.println("invalid title!");
            return true;
        } else if (isDateInvalid(startDateString)) {
            System.out.println("invalid start date!");
            return true;
        } else
            return false;
    }

    private boolean isAddEntryFormatInvalid(Matcher matcher, int startDateGroupIndex) {
        String title = matcher.group(1);
        String startDateString = matcher.group(startDateGroupIndex);

        if (isTitleOrStartDateInvalid(title, startDateString))
            return true;
        else if (matcher.group(startDateGroupIndex + 2) != null) { // has repeat
            if (!matcher.group(startDateGroupIndex + 1).matches("\\d+")) { // has end date
                String endDateString = matcher.group(startDateGroupIndex + 1);
                if (isDateInvalid(endDateString)) {
                    System.out.println("invalid end date!");
                    return true;
                }
            }
        }
        return false;
    }

    private Event createEvent(Matcher matcher, int startDateGroupIndex) {
        String title = matcher.group(1);
        String startDateString = matcher.group(startDateGroupIndex);
        String hasMeetingString = matcher.group(startDateGroupIndex + 3);
        LocalDate startDate = LocalDate.from(dateFormatter.parse(startDateString));
        Event event;

        if (matcher.group(startDateGroupIndex + 2) != null) { // has repeat
            RepeatPeriod repeatPeriod =
                    RepeatPeriod.getRepeatPeriodByMarker(matcher.group(startDateGroupIndex + 2));

            if (matcher.group(startDateGroupIndex + 1).matches("\\d+")) { // has repeat number
                long repeatNumber = Long.parseLong(matcher.group(startDateGroupIndex + 1));
                event = new Event(title, startDate, repeatNumber, repeatPeriod);
            } else { // has end date
                String endDateString = matcher.group(startDateGroupIndex + 1);
                LocalDate endDate = LocalDate.from(dateFormatter.parse(endDateString));
                event = new Event(title, startDate, endDate, repeatPeriod);
            }
        } else
            event = new Event(title, startDate);

        setMeetingLinkIfEventHasMeeting(event, hasMeetingString);
        return event;
    }

    private void setMeetingLinkIfEventHasMeeting(Event event, String hasMeetingString) {
        if (hasMeetingString.equals("T"))
            event.setMeetingLink(inputStream.nextLine());
    }

    private void addEvent(Matcher matcher) {
        if (!hasAccess) {
            System.out.println("you don't have access to do this!");
            return;
        }

        if (isAddEntryFormatInvalid(matcher, 2))
            return;

        String title = matcher.group(1);
        if (calendar.getEventByTitle(title) != null) {
            System.out.println("there is another event with this title!");
            return;
        }

        Event event = createEvent(matcher, 2);
        calendar.addEvent(event);
        System.out.println("event added successfully!");
    }

    private Task createTask(Matcher matcher) {
        Event event = createEvent(matcher, 4);
        LocalTime startTime = LocalTime.from(timeFormatter.parse(matcher.group(2)));
        LocalTime endTime = LocalTime.from(timeFormatter.parse(matcher.group(3)));

        return new Task(event, startTime, endTime);
    }

    private void addTask(Matcher matcher) {
        if (!hasAccess) {
            System.out.println("you don't have access to do this!");
            return;
        }

        if (isAddEntryFormatInvalid(matcher, 4))
            return;

        String title = matcher.group(1);
        if (calendar.getTaskByTitle(title) != null) {
            System.out.println("there is another task with this title!");
            return;
        }

        Task task = createTask(matcher);
        calendar.addTask(task);
        System.out.println("task added successfully!");
    }

    private void editEvent(Matcher matcher) {
        if (!hasAccess) {
            System.out.println("you don't have access to do this!");
            return;
        }

        String title = matcher.group(1);
        if (isEntryTitleFormatInvalid(title)) {
            System.out.println("invalid title!");
            return;
        }
        if (calendar.getEventByTitle(title) == null) {
            System.out.println("there is no event with this title!");
            return;
        }
        Map<String, BiConsumer<Event, String>> editEventCommandMap = createEditEventCommandMap();

        String editField = matcher.group(2);
        String newField = matcher.group(3);
        Event event = calendar.getEventByTitle(title);

        editEventCommandMap.get(editField).accept(event, newField);
        System.out.println("event edited!");
    }

    private Map<String, BiConsumer<Event, String>> createEditEventCommandMap() {
        Map<String, BiConsumer<Event, String>> editEventCommandMap = new HashMap<>();

        editEventCommandMap.put("title", this::editEventTitle);
        editEventCommandMap.put("repeat", this::editEventRepeat);
        editEventCommandMap.put("kind of repeat", this::editEventRepeatPeriod);
        editEventCommandMap.put("meet", this::editEventMeetingLink);

        return editEventCommandMap;
    }

    private void editEventTitle(Event event, String newTitle) {
        if (isEntryTitleFormatInvalid(newTitle))
            System.out.println("invalid title!");
        else if (calendar.getEventByTitle(newTitle) != null)
            System.out.println("there is another event with this title!");
        else
            calendar.editEventTitle(event, newTitle);
    }

    private void editEventRepeat(Event event, String newRepeat) {
        if (newRepeat.matches("\\d+")) {
            long repeatNumber = Long.parseLong(newRepeat);
            event.setDatesByRepeatNumber(repeatNumber);
        } else if (isEntryTitleFormatInvalid(newRepeat))
            System.out.println("invalid end date!");
        else {
            LocalDate endDate = LocalDate.from(dateFormatter.parse(newRepeat));
            event.setDatesByEndDate(endDate);
        }
    }

    private void editEventRepeatPeriod(Event event, String newRepeatPeriodMarker) {
        RepeatPeriod newRepeatPeriod = RepeatPeriod.getRepeatPeriodByMarker(newRepeatPeriodMarker);
        event.setDatesByRepeatPeriod(newRepeatPeriod);
    }

    private void editEventMeetingLink(Event event, String newHasMeetingString) {
        if (newHasMeetingString.equals("T"))
            event.setMeetingLink(inputStream.nextLine());
        else
            event.setMeetingLink(null);
    }

    private void editTask(Matcher matcher) {
        if (!hasAccess) {
            System.out.println("you don't have access to do this!");
            return;
        }

        String title = matcher.group(1);
        if (isEntryTitleFormatInvalid(title)) {
            System.out.println("invalid title!");
            return;
        }
        if (calendar.getTaskByTitle(title) == null) {
            System.out.println("there is no task with this title!");
            return;
        }
        Map<String, BiConsumer<Task, String>> editTaskCommandMap = createEditTaskCommandMap();

        String editField = matcher.group(2);
        String newField = matcher.group(3);
        Task task = calendar.getTaskByTitle(title);

        editTaskCommandMap.get(editField).accept(task, newField);
        System.out.println("task edited!");
    }

    private Map<String, BiConsumer<Task, String>> createEditTaskCommandMap() {
        Map<String, BiConsumer<Task, String>> editTaskCommandMap = new HashMap<>();

        editTaskCommandMap.put("title", this::editTaskTitle);
        editTaskCommandMap.put("repeat", this::editTaskRepeat);
        editTaskCommandMap.put("kind of repeat", this::editTaskRepeatPeriod);
        editTaskCommandMap.put("meet", this::editTaskMeetingLink);
        editTaskCommandMap.put("start time", this::editTaskStartTime);
        editTaskCommandMap.put("end time", this::editTaskEndTime);

        return editTaskCommandMap;
    }

    private void editTaskTitle(Task task, String newTitle) {
        if (isEntryTitleFormatInvalid(newTitle))
            System.out.println("invalid title!");
        else if (calendar.getTaskByTitle(newTitle) != null)
            System.out.println("there is another task with this title!");
        else
            calendar.editTaskTitle(task, newTitle);
    }

    private void editTaskRepeat(Task task, String newRepeat) {
        editEventRepeat(task, newRepeat);
    }

    private void editTaskRepeatPeriod(Task task, String newRepeatPeriodMarker) {
        editEventRepeatPeriod(task, newRepeatPeriodMarker);
    }

    private void editTaskMeetingLink(Task task, String newMeetingLink) {
        editEventMeetingLink(task, newMeetingLink);
    }

    private void editTaskStartTime(Task task, String newStartTimeString) {
        LocalTime newStartTime = LocalTime.from(timeFormatter.parse(newStartTimeString));
        task.setStartTime(newStartTime);
    }

    private void editTaskEndTime(Task task, String newEndTimeString) {
        LocalTime newEndTime = LocalTime.from(timeFormatter.parse(newEndTimeString));
        task.setEndTime(newEndTime);
    }

    private void removeEvent(Matcher matcher) {
        if (!hasAccess) {
            System.out.println("you don't have access to do this!");
            return;
        }

        String title = matcher.group(1);

        if (isEntryTitleFormatInvalid(title))
            System.out.println("invalid title!");
        else if (calendar.getEventByTitle(title) == null)
            System.out.println("there is no event with this title!");
        else {
            calendar.removeEventByTitle(title);
            System.out.println("event deleted successfully!");
        }
    }

    private void removeTask(Matcher matcher) {
        if (!hasAccess) {
            System.out.println("you don't have access to do this!");
            return;
        }

        String title = matcher.group(1);

        if (isEntryTitleFormatInvalid(title))
            System.out.println("invalid title!");
        else if (calendar.getTaskByTitle(title) == null)
            System.out.println("there is no task with this title!");
        else {
            calendar.removeTaskByTitle(title);
            System.out.println("task deleted successfully!");
        }
    }

    private void printEvents() {
        Event[] sortedEvents = calendar.getSortedEvents();
        if (sortedEvents.length > 0) {
            System.out.println("events of this calendar:");
            System.out.println(arrayToLineBreakSeparatedString(sortedEvents));
        } else
            System.out.println("nothing");
    }

    private void printTasks() {
        Task[] sortedTasks = calendar.getSortedTasks();
        if (sortedTasks.length > 0) {
            System.out.println("tasks of this calendar:");
            System.out.println(arrayToLineBreakSeparatedString(sortedTasks));
        } else
            System.out.println("nothing");
    }
}
