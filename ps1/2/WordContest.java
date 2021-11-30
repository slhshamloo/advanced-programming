import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;


public class WordContest {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int numOfContestants = in.nextInt();
        in.nextLine(); // Avoid getting empty input for future nextLine()
        
        String[] answer = runContest(numOfContestants, in);
        printAnswer(answer);

        in.close();
    }

    public static String[] runContest(int numOfContestants,
            Scanner inputStream) {
        List<Set<String>> words = new LinkedList<Set<String>>();

        constructList(words, numOfContestants, inputStream);
        removeWords(words, numOfContestants, inputStream);
        Set<String> intersection = findIntersection(words);

        String[] sorted = sortAnswer(intersection);
        return sorted;
    }

    public static void constructList(List<Set<String>> words, int numOfContestants,
            Scanner inputStream) {
        for (int i = 0; i < numOfContestants; i++) {
            String[] line = inputStream.nextLine().toLowerCase().split(" ");
            // Using sets to remove duplicates
            Set<String> wordSet = new HashSet<String>(Arrays.asList(line));
            words.add(wordSet);
        }
    }

    public static void removeWords(List<Set<String>> words, int numOfDays,
            Scanner inputStream) {
        for (int i = 0; i < numOfDays; i++) {
            String[] line = inputStream.nextLine().toLowerCase().split(" ");
            // Using sets to remove duplicates
            Set<String> removeSet = new HashSet<String>(Arrays.asList(line));
            removeFromList(words, removeSet);
        }
    }
    public static void removeFromList(List<Set<String>> words, Set<String> removeSet) {
        for (Set<String> wordSet: words)
            wordSet.removeAll(removeSet);
    }

    public static Set<String> findIntersection(List<Set<String>> words) {
        Set<String> intersection = words.get(0);
        for (Set<String> wordSet: words)
            intersection.retainAll(wordSet);
        return intersection;
    }

    public static String[] sortAnswer(Set<String> answer) {
        String[] sorted = new String[answer.size()];
        answer.toArray(sorted);
        
        Arrays.sort(sorted, Collections.reverseOrder());
        return sorted;
    }

    public static void printAnswer(String[] wordSet) {
        if (wordSet.length == 0)
            System.out.print("Nothing in common");
        else
            for (String word: wordSet)
                System.out.print(word + " ");
    }
}