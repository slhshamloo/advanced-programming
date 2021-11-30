package controllers;

import calendar.MahmoudiCalendar;
import calendar.entry.Event;
import calendar.entry.Task;
import user.Database;
import user.User;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static controllers.CalendarController.dateFormatter;
import static controllers.CalendarController.isDateInvalid;

public class MainMenu extends AbstractController {
    private final User user;
    private final Database database;
    private final Set<Integer> enabledCalendarIds = new HashSet<>();

    public MainMenu(Scanner inputStream, User user, Database database) {
        super(inputStream, "Logout");

        this.user = user;
        this.database = database;

        user.createFirstCalendarForFirstLogin(database);
        enableFirstCalender();
    }

    private static boolean isCalendarTitleFormatInvalid(String title) {
        return !(title.matches("\\w+"));
    }

    private static boolean isCalendarIdTooBig(String calendarIdString) {
        return calendarIdString.length() > 9;
    }

    private void enableFirstCalender() {
        List<Integer> accessedCalendarIdList = new ArrayList<>(user.getOwnedCalendarIdList());
        accessedCalendarIdList.addAll(user.getSharedCalendarIdList());
        accessedCalendarIdList.sort(Comparator.naturalOrder());
        for (int id : accessedCalendarIdList)
            if (database.getCalendarById(id).getTitle().equals(user.getUsername())) {
                enabledCalendarIds.add(id);
                return;
            }
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("Create New Calendar (\\S+)"), this::createCalendar);
        commandMap.put(Pattern.compile("Open Calendar (\\d+)"), this::openCalendar);
        commandMap.put(Pattern.compile("Enable Calendar (\\d+)"), this::enableCalendar);
        commandMap.put(Pattern.compile("Disable Calendar (\\d+)"), this::disableCalendar);
        commandMap.put(Pattern.compile("Delete Calendar (\\d+)"), this::deleteCalendar);
        commandMap.put(Pattern.compile("Remove Calendar (\\d+)"), this::removeCalendar);
        commandMap.put(Pattern.compile("Edit Calendar (\\d+) (\\S+)"), this::editCalendar);
        commandMap.put(Pattern.compile("Share Calendar (\\d+) (.+)"), this::shareCalendar);
        commandMap.put(Pattern.compile("Show (\\d{4}_\\d{2}_\\d{2})"), this::printDateEntries);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("Show Calendars", this::printCalendars);
        noArgumentCommandMap.put("Show Enabled Calendars", this::printEnabledCalendars);

        return noArgumentCommandMap;
    }

    @Override
    protected void escape() {
        System.out.println("logout successful");
    }

    private void createCalendar(Matcher matcher) {
        String title = matcher.group(1);

        if (isCalendarTitleFormatInvalid(title))
            System.out.println("invalid title!");
        else {
            int id = database.addCalendar(title);
            user.addOwnedCalenderId(id);
            System.out.println("calendar created successfully!");
        }
    }

    private boolean isCalendarCreatedAndUserHasIt(int id) {
        if (database.getCalendarById(id) == null) {
            System.out.println("there is no calendar with this ID!");
            return false;
        } else if (!user.isCalendarOwned(id) && !user.isCalendarShared(id)) {
            System.out.println("you have no calendar with this ID!");
            return false;
        }
        return true;
    }

    private void openCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));

        if (isCalendarCreatedAndUserHasIt(id)) {
            MahmoudiCalendar calendar = database.getCalendarById(id);
            Controller calendarController = new CalendarController(
                    inputStream, calendar, user.isCalendarOwned(id));

            System.out.println("calendar opened successfully!");
            calendarController.run();
        }
    }

    private void enableCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));

        if (isCalendarCreatedAndUserHasIt(id)) {
            enabledCalendarIds.add(id);
            System.out.println("calendar enabled successfully!");
        }
    }

    private void disableCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));

        if (isCalendarCreatedAndUserHasIt(id)) {
            enabledCalendarIds.remove(id);
            System.out.println("calendar disabled successfully!");
        }
    }

    private void deleteCalendarById(int id) {
        database.removeCalendarById(id);
        enabledCalendarIds.remove(id);
        System.out.println("calendar deleted!");
    }

    private void deleteCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));

        if (isCalendarCreatedAndUserHasIt(id)) {
            if (!user.isCalendarOwned(id))
                System.out.println("you don't have access to delete this calendar!");
            else
                deleteCalendarById(id);
        }
    }

    private void removeCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));

        if (isCalendarCreatedAndUserHasIt(id)) {
            if (user.isCalendarOwned(id)) {
                System.out.println("do you want to delete this calendar?");
                String answer = removeExtraWhitespace(inputStream.nextLine());
                if (answer.equals("yes"))
                    deleteCalendarById(id);
                else if (answer.equals("no"))
                    System.out.println("OK!");
            } else {
                user.removeSharedCalenderId(id);
                enabledCalendarIds.remove(id);
                System.out.println("calendar removed!");
            }
        }
    }

    private void editCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));
        String title = matcher.group(2);

        if (isCalendarCreatedAndUserHasIt(id)) {
            if (!user.isCalendarOwned(id))
                System.out.println("you don't have access to edit this calendar!");
            else if (isCalendarTitleFormatInvalid(title))
                System.out.println("invalid title!");
            else {
                database.getCalendarById(id).setTitle(title);
                System.out.println("calendar title edited!");
            }
        }
    }

    private void shareCalendar(Matcher matcher) {
        if (isCalendarIdTooBig(matcher.group(1))) {
            System.out.println("there is no calendar with this ID!");
            return;
        }
        int id = Integer.parseInt(matcher.group(1));
        String[] usernamesToShareWith = matcher.group(2).split(" ");

        if (isCalendarCreatedAndUserHasIt(id)) {
            if (!user.isCalendarOwned(id))
                System.out.println("you don't have access to share this calendar!");
            else {
                for (String username : usernamesToShareWith)
                    if (LoginMenu.isUsernameFormatInvalid(username)) {
                        System.out.println("invalid username!");
                        return;
                    }

                Set<User> usersToShareWith = Arrays.stream(usernamesToShareWith)
                        .map(database::getUserByUsername).collect(Collectors.toSet());

                if (usersToShareWith.contains(null))
                    System.out.println("no user exists with this username!");
                else {
                    usersToShareWith.forEach(
                            (userToShareWith) -> userToShareWith.addSharedCalenderId(id));
                    System.out.println("calendar shared!");
                }
            }
        }
    }

    private void printDateEntries(Matcher matcher) {
        String dateString = matcher.group(1);
        LocalDate date;

        if (isDateInvalid(dateString))
            System.out.println("date is invalid!");
        else {
            date = LocalDate.from(dateFormatter.parse(dateString));

            List<Event> eventList = getSortedEventsOnDate(date);
            System.out.println("events on " + dateString + ":");
            if (eventList.size() > 0)
                System.out.println(
                        arrayToLineBreakSeparatedString(eventList.toArray(new Event[0])));

            List<Task> taskList = getSortedTasksOnDate(date);
            System.out.println("tasks on " + dateString + ":");
            if (taskList.size() > 0)
                System.out.println(
                        arrayToLineBreakSeparatedString(taskList.toArray(new Task[0])));
        }
    }

    private List<Event> getSortedEventsOnDate(LocalDate date) {
        List<Event> eventList = new ArrayList<>();

        for (MahmoudiCalendar calendar :
                database.getCalendarsByIds(new ArrayList<>(enabledCalendarIds)))
            eventList.addAll(calendar.getEventListOnDate(date));

        eventList.sort(Comparator.comparing(Event::getTitle));
        return eventList;
    }

    private List<Task> getSortedTasksOnDate(LocalDate date) {
        List<Task> taskList = new ArrayList<>();

        for (MahmoudiCalendar calendar :
                database.getCalendarsByIds(new ArrayList<>(enabledCalendarIds)))
            taskList.addAll(calendar.getTaskListOnDate(date));

        taskList.sort(Comparator.comparing(Task::getTitle));
        taskList.sort(Comparator.comparing(Task::getStartTime));
        return taskList;
    }

    private void printCalendars() {
        MahmoudiCalendar[] calendars =
                database.getCalendarsByIds(user.getOwnedCalendarIdList());

        printLineBreakSeparatedArray(calendars);
    }

    private void printEnabledCalendars() {
        ArrayList<Integer> sortedEnabledCalendarList = new ArrayList<>(enabledCalendarIds);

        sortedEnabledCalendarList.sort(Comparator.naturalOrder());
        MahmoudiCalendar[] calendars = database.getCalendarsByIds(sortedEnabledCalendarList);

        printLineBreakSeparatedArray(calendars);
    }
}
