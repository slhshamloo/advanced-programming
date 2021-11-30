import java.util.Scanner;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;


public class RandomMazeGenerator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int length = in.nextInt();
        int trials = in.nextInt();

        generateMazes(width, length, trials);
        in.close();
    }

    public static void generateMazes(int width, int length, int trials) {
        char[][] blankMaze = initMaze(width, length);
        while(trials > 0) {
            char[][] maze = new char[2 * width + 1][2 * length + 1];
            copyMaze(blankMaze, maze);
            makePath(maze, width, length);
            printMaze(maze);
            trials--;
        }
    }

    public static char[][] initMaze(int width, int length) {
        char[][] maze = new char[2 * width + 1][2 * length + 1];
        for (char[] row: maze)
            Arrays.fill(row, '1');
        
        maze[0][1] = 'e';
        maze[2 * width][2 * length - 1] = 'e';
        
        return maze;
    }

    public static void copyMaze(char[][] source, char[][] destination) {
        for (int i = 0; i < source.length; i++)
            for (int j = 0; j < source[0].length; j++)
                destination[i][j] = source[i][j];
    }

    public static void makePath(char[][] maze, int width, int length) {
        LinkedList<int[]> stack = new LinkedList<int[]>();

        int randomX = ThreadLocalRandom.current().nextInt(0, width);
        int randomY = ThreadLocalRandom.current().nextInt(0, length);
        int[] initPos = {randomX, randomY};
        stack.add(initPos);
        // visited location = '*', unvisited location = '1'
        maze[2 * randomX + 1][2 * randomY + 1] = '*';

        while(!stack.isEmpty()) {
            int[] pos = stack.getLast();
            LinkedList<int[]> neighbors = findNeighbors(pos, maze, width, length);
            
            if (neighbors.size() == 0)
                stack.removeLast();
            else {
                int choice = ThreadLocalRandom.current().nextInt(0, neighbors.size());
                int[] nextPos = neighbors.get(choice);
                stack.add(nextPos);

                maze[2 * nextPos[0] + 1][2 * nextPos[1] + 1] = '*';
                // remove wall (wall = '1', no wall = '0')
                maze[pos[0] + nextPos[0] + 1][pos[1] + nextPos[1] + 1] = '0';
            }
        }
    }

    public static LinkedList<int[]> findNeighbors(int[] pos, char[][] maze,
            int width, int length) {
        LinkedList<int[]> neighbors = new LinkedList<int[]>();
        // avoid borders and visited locations
        // unvisited location = '1'
        if (pos[0] > 0 && maze[2 * pos[0] - 1][2* pos[1] + 1] == '1') {
            int[] neighbor = {pos[0] - 1, pos[1]};
            neighbors.add(neighbor);
        }
        if (pos[0] < width - 1 && maze[2 * pos[0] + 3][2* pos[1] + 1] == '1') {
            int[] neighbor = {pos[0] + 1, pos[1]};
            neighbors.add(neighbor);
        }
        if (pos[1] > 0 && maze[2 * pos[0] + 1][2* pos[1] - 1] == '1') {
            int[] neighbor = {pos[0], pos[1] - 1};
            neighbors.add(neighbor);
        }
        if (pos[1] < length - 1 && maze[2 * pos[0] + 1][2* pos[1] + 3] == '1') {
            int[] neighbor = {pos[0], pos[1] + 1};
            neighbors.add(neighbor);
        }

        return neighbors;
    }
    public static void printMaze(char[][] maze) {
        // convert maze to string for faster printing
        String output = Arrays.deepToString(maze).replace("], ", "\n").replace(", ", "")
            .replace("[", "").replace("]", "");
        System.out.println(output);
        System.out.println();
    }
}
