import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.*;


public class MessageHandler {
    static final String VALID_FORMAT_REGEX = "Message\\{ "
        + "messageId=%[\\d\\s]+-(?=[^THOMASthomas]{5})[A-Z][a-z]{4}\\$(?:\\d{4}|\\d{2})%, "
        + "from=User\\{ firstName='(?<first>[^']+)', "
        + "isBot=(?<bot>true|false), lastName='(?<last>[^']*)', "
        + "userName='(?:[a-zA-Z]\\w{3,30}[a-zA-Z0-9])?' \\}, "
        + "date=(?<date>\\d{14}), text='(?<text>[^']*)', location=(?<loc>-?\\d+\\.?\\d*) \\}";
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String messages = in.nextLine();
        int beginDate = in.nextInt();
        int endDate = in.nextInt();
        String locString = in.next();

        List<String> validMessages = findValidMessages(messages, beginDate, endDate, locString);
        printMessages(validMessages);
        in.close();
    }

    public static List<String> findValidMessages(String messages,
            int beginDate, int endDate, String tomLocation) {
        List<String> validMessages = new LinkedList<String>();
        boolean botFlag = false;

        Pattern pattern = Pattern.compile(VALID_FORMAT_REGEX);
        Matcher matcher = pattern.matcher(messages);
        
        while(matcher.find()) {
            if (matcher.group("bot").equals("true")) {
                botFlag = true;
                continue;
            }
            if (botFlag) {
                botFlag = false;
                continue;
            }
            String messageLocation = matcher.group("loc");
            if (isCloseEnough(tomLocation, messageLocation)) {
                String messageTime = matcher.group("date");
                if (isInDateRange(messageTime, beginDate, endDate)) {
                    String name = matcher.group("first") + " " + matcher.group("last");
                    String text = matcher.group("text");
                    String message = writeMessage(name, text, messageTime);
                    validMessages.add(message);
                }
            }
        }
        return validMessages;
    }

    public static boolean isCloseEnough(String location1, String location2) {
        double distance = Double.parseDouble(location1) - Double.parseDouble(location2);
        
        if (distance >= -1 && distance <= 1)
            return true;
        else
            return false;
    }

    public static boolean isInDateRange(String messageTime, int beginDate, int endDate) {
        int messageDate = Integer.parseInt(messageTime.substring(0, 8));

        if (endDate >= messageDate && messageDate >= beginDate)
            return true;
        else 
            return false;
    }

    public static String writeMessage(String name, String text, String messageTime) {
        String clockTime = messageTime.substring(8, 10) + ":" + messageTime.substring(10, 12);

        return "--------------------\n"
            + "*" + name + "*" + "\n"
            + text + "\n"
            + "_" + clockTime + "_" + '\n'
            + "--------------------";
    }

    public static void printMessages(List<String> messages) {
        for (String message: messages)
            System.out.println(message);
    }
}
