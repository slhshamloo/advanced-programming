package controllers;

import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractController implements controllers.Controller {
    protected static final String INVALID_COMMAND_STRING = "invalid command!";
    protected final Scanner inputStream;
    private final Map<Pattern, Consumer<Matcher>> commandMap = createCommandMap();
    private final Map<String, Runnable> noArgumentCommandMap = createNoArgumentCommandMap();
    private Function<String, Boolean> isEscapeConditionTrue;

    public AbstractController(Scanner inputStream) {
        this.inputStream = inputStream;
    }

    public AbstractController(Scanner inputStream, String escapeCommand) {
        this(inputStream, (input) -> input.equals(escapeCommand));
    }

    public AbstractController(Scanner inputStream,
            Function<String, Boolean> isEscapeConditionTrue) {
        this.inputStream = inputStream;
        this.isEscapeConditionTrue = isEscapeConditionTrue;
    }

    public static String arrayToLineBreakSeparatedString(Object[] array) {
        StringJoiner stringJoiner = new StringJoiner("\n");
        for (Object object : array)
            stringJoiner.add(object.toString());
        return stringJoiner.toString();
    }

    public static void printLineBreakSeparatedArray(Object[] objects) {
        if (objects.length > 0)
            System.out.println(arrayToLineBreakSeparatedString(objects));
        else
            System.out.println("nothing");
    }

    public static String removeExtraWhitespace(String string) {
        return string.trim().replaceAll("\\s+", " ");
    }

    protected void setEscapeCondition(Function<String, Boolean> isEscapeConditionTrue) {
        this.isEscapeConditionTrue = isEscapeConditionTrue;
    }

    public void run() {
        String input = removeExtraWhitespace(inputStream.nextLine());

        while (!isEscapeConditionTrue.apply(input)) {
            runCommand(input);
            if (isEscapeConditionTrue.apply(input)) // no new input needed
                break;
            input = removeExtraWhitespace(inputStream.nextLine());
        }

        escape();
    }

    private void runCommand(String input) {
        Matcher commandMatcher = findCommand(input, commandMap);
        if (commandMatcher != null) {
            Pattern commandPattern = commandMatcher.pattern();
            Consumer<Matcher> commandFunction = commandMap.get(commandPattern);
            commandFunction.accept(commandMatcher);
        } else { // command not found among consumers
            Runnable commandFunction = findNoArgumentCommand(input, noArgumentCommandMap);
            if (commandFunction != null)
                commandFunction.run();
            else // command not found
                System.out.println(INVALID_COMMAND_STRING);
        }
    }

    private Matcher findCommand(String input, Map<Pattern, Consumer<Matcher>> commandMap) {
        for (Pattern command : commandMap.keySet()) {
            Matcher matcher = command.matcher(input);
            if (matcher.matches())
                return matcher;
        }
        return null; // command not found among consumers
    }

    private Runnable findNoArgumentCommand(String input,
            Map<String, Runnable> noArgumentCommandMap) {
        return noArgumentCommandMap.get(input);
    }

    abstract protected Map<Pattern, Consumer<Matcher>> createCommandMap();

    abstract protected Map<String, Runnable> createNoArgumentCommandMap();

    abstract protected void escape();
}
