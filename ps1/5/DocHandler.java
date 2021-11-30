import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;


public class DocHandler {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Map<String, String> docs = new HashMap<>();

        String command = in.nextLine().trim();
        while(!command.equals("END")) {
            takeCommand(command, docs, in);
            command = in.nextLine().trim();
        }
        in.close();
    }

    public static void takeCommand(String command, Map<String, String> docs,
            Scanner inputStream) {
        if (command.startsWith("ADD DOC ")) {   // Special treatment for this function
            addDocument(command, docs, inputStream);
            return;
        }

        Map<String, BiConsumer<String, Map<String, String>>> commandMap = new HashMap<>();
        commandMap.put("RMV DOC ", DocHandler::removeDocument);
        commandMap.put("PRINT ", DocHandler::printDocument);
        commandMap.put("RPLC ", DocHandler::replaceWords);
        commandMap.put("RMV WORD ", DocHandler::removeWord);
        commandMap.put("ADD WORD ", DocHandler::addWord);
        commandMap.put("FIND REP ", DocHandler::countReps);
        commandMap.put("FIND MIRROR ", DocHandler::countMirrors);
        commandMap.put("FIND ALPHABET WORDS ", DocHandler::countAlphabeticalWords);
        commandMap.put("GCD ", DocHandler::findGCD);

        for (String commandName: commandMap.keySet())
            if (command.startsWith(commandName)) {
                commandMap.get(commandName).accept(command, docs);
                return;
            }
        
        // if command was not found in commandMap
        System.out.println("invalid command!");
    }

    public static void addDocument(String command, Map<String, String> docs, Scanner inputStream) {
        String docName = command.substring(8); // After "ADD DOC "
        if (docName.contains(" ")) {    // Input too big
            System.out.println("invalid command!");
            return;
        }
        String content = inputStream.nextLine();

        content = clean(content);
        docs.put(docName, content);
    }

    public static String clean(String input) {
        final String LINK_PATTERN = "!?\\[(\\S+)\\]\\(.*?\\)";
        final String BOLD_PATTERN = "(?<!\\S)(?:\\*{2})+([^*]+)(?:\\*{2})+(?!\\S)";
        final String NOISE_PATTERN = "\\S*[^a-zA-Z0-9\\s]+\\S*";
        
        // Apply link replacement until no links are left (for nested links)
        String newInput = input.replaceAll(LINK_PATTERN, "$1");
        while (!newInput.equals(input)) {
            input = newInput;
            newInput = input.replaceAll(LINK_PATTERN, "$1");
        }
        input = input.replaceAll(BOLD_PATTERN, "$1");
        input = input.replaceAll(NOISE_PATTERN, "");

        return input;
    }

    public static void printDocument(String command, Map<String, String> docs) {
        String docName = command.substring(6); // After "PRINT "

        if (docName.contains(" "))  // Input too big
            System.out.println("invalid command!");
        else if (docs.containsKey(docName))
            System.out.println(docs.get(docName));
        else
            System.out.println("invalid file name!");
    }

    public static void removeDocument(String command, Map<String, String> docs) {
        String docName = command.substring(8); // After "RMV DOC "

        if (docName.contains(" "))  // Input too big
            System.out.println("invalid command!");
        else if (docs.containsKey(docName))
            docs.remove(docName);
        else
            System.out.println("invalid file name!");
    }

    public static void replaceWords(String command, Map<String, String> docs) {
        Scanner scanner = new Scanner(command.substring(5)); // Skip "RPLC "

        String docName = scanner.next();
        if (!scanner.hasNext()) {   // Input too small
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        String[] words = scanner.next().split(",");
        if (!scanner.hasNext()) {   // Input too small
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        String replaceWord = scanner.next();
        if (scanner.hasNext()) {    // Input too big
            System.out.println("invalid command!");
            scanner.close();
            return;
        }

        if (docName.equals("-ALL"))
            for (String doc: docs.keySet())
                replaceLast(words, replaceWord, doc, docs);
        else if (docs.containsKey(docName))
            replaceLast(words, replaceWord, docName, docs);
        else
            System.out.println("invalid file name!");
        
        scanner.close();
    }

    public static void replaceLast(String[] words, String replaceWord,
            String docName, Map<String, String> docs) {
        String content = docs.get(docName);
        
        for (String word: words) {
            Pattern pattern = Pattern.compile("(?<!\\S)" + word + "(?!\\S)");
            Matcher matcher = pattern.matcher(content);
            int wordLastIndex = -1;
            
            while (matcher.find())
                wordLastIndex = matcher.start();
            if (wordLastIndex != -1)
                content = content.substring(0, wordLastIndex) + replaceWord
                    + content.substring(wordLastIndex + word.length());
        }    
        
        docs.put(docName, content);
    }

    public static void removeWord(String command, Map<String, String> docs) {
        Scanner scanner = new Scanner(command.substring(9)); // Skip "RMV WORD "

        String docName = scanner.next();
        if (!scanner.hasNext()) {   // Input too small
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        String word = scanner.next();
        if (scanner.hasNext()) {    // Input too big
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        
        if (docName.equals("-ALL"))
            for (String doc: docs.keySet())
                removeAllOccurrences(word, doc, docs);
        else if (docs.containsKey(docName))
            removeAllOccurrences(word, docName, docs);
        else
            System.out.println("invalid file name!");
        
        scanner.close();
    }

    public static void removeAllOccurrences(String word, String docName,
            Map<String, String> docs) {
        String content = docs.get(docName);
        content = content.replaceAll("(?<!\\w)" + word + "(?!\\w)", "");
        docs.put(docName, content);
    }

    public static void addWord(String command, Map<String, String> docs) {
        Scanner scanner = new Scanner(command.substring(9)); // Skip "ADD WORD "

        String docName = scanner.next();
        if (!scanner.hasNext()) {   // Input too small
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        String word = scanner.next();
        if (scanner.hasNext()) {    // Input too big
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        
        if (docName.equals("-ALL"))
            for (String doc: docs.keySet())
                appendWordToDoc(word, doc, docs);
        else if (docs.containsKey(docName))
            appendWordToDoc(word, docName, docs);
        else
            System.out.println("invalid file name!");
        
        scanner.close();
    }

    public static void appendWordToDoc(String word, String docName, Map<String, String> docs) {
        String content = docs.get(docName);
        content += word;
        docs.put(docName, content);
    }

    public static void countReps(String command, Map<String, String> docs) {
        Scanner scanner = new Scanner(command.substring(9)); // Skip "FIND REP "

        String docName = scanner.next();
        if (!scanner.hasNext()) {   // Input too small
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        String word = scanner.next();
        if (scanner.hasNext()) {    // Input too big
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        if (!word.matches("[a-zA-Z0-9]+")) {
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        
        if (docs.containsKey(docName))
            printRepCount(word, docName, docs);
        else
            System.out.println("invalid file name!");
        
        scanner.close();
    }

    public static void printRepCount(String word, String docName, Map<String, String> docs) {
        int count = 0;
        String content = docs.get(docName);
        int index = content.indexOf(word);

        while (index != -1) {
            count++;
            index++;
            index = content.indexOf(word, index);
        }

        System.out.println(word + " is repeated " + Integer.toString(count)
            + " times in " + docName);
    }

    public static void countMirrors(String command, Map<String, String> docs) {
        Scanner scanner = new Scanner(command.substring(12)); // Skip "FIND MIRROR "

        String docName = scanner.next();
        if (!scanner.hasNext()) {   // Input too small
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        String character = scanner.next();
        if (scanner.hasNext()) {    // Input too big
            System.out.println("invalid command!");
            scanner.close();
            return;
        }
        
        if (!character.matches("[a-zA-Z]")) {   // Not a character
            System.out.println("invalid command!");
            scanner.close();
            return;
        }

        if (docs.containsKey(docName))
            printMirrorCount(character, docs.get(docName));
        else
            System.out.println("invalid file name!");
        
        scanner.close();
    }

    public static void printMirrorCount(String character, String doc) {
        Pattern pattern = Pattern.compile(
            "(?<![a-zA-Z])(\\d+)" + character + "(\\d+)(?![a-zA-Z])");
        Matcher matcher = pattern.matcher(doc);
        int count = 0;

        while(matcher.find()) {
            int num1 = Integer.parseInt(matcher.group(1));
            int num2 = Integer.parseInt(matcher.group(2));
            if(num1 == num2)
                count++;
        }
        
        System.out.println(Integer.toString(count) + " mirror words!");
    }

    public static void countAlphabeticalWords(String command, Map<String, String> docs) {
        String docName = command.substring(20);
        if (docName.contains(" ")) {    // Input too big
            System.out.println("invalid command!");
            return;
        }
        if (!docs.containsKey(docName)) {
            System.out.println("invalid file name!");
            return;
        }
        
        Pattern pattern = Pattern.compile("(?<!\\d)[a-zA-Z]+(?!\\d)");
        Matcher matcher = pattern.matcher(docs.get(docName));
        int count = 0;

        while(matcher.find())
            count++;

        System.out.println(Integer.toString(count) + " alphabetical words!");
    }

    public static void findGCD(String command, Map<String, String> docs) {
        String docName = command.substring(4); // Skip " GCD "
        if (docName.contains(" ")) {    // Input too big
            System.out.println("invalid command!");
            return;
        }
        if (!docs.containsKey(docName)) {
            System.out.println("invalid file name!");
            return;
        }
        String content = docs.get(docName);
        List<Integer> numbers = new ArrayList<Integer>();
        
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(docs.get(docName));

        while(matcher.find()) {
            String number = matcher.group(1);
            numbers.add(Integer.parseInt(number));
        }
        
        if (!numbers.isEmpty()) {
            content += Integer.toString(gcd(numbers));
            docs.put(docName, content);
        }
    }

    public static int gcd(List<Integer> numbers) {
        if (numbers.size() == 1)
            return numbers.get(0);
        
        int first = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            int second = numbers.get(i);
            while (second != 0) {
                int temp = second;
                second = first % second;
                first = temp;
            }
        }
        return first;
    }
}
