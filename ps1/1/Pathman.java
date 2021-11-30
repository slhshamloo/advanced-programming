import java.util.Scanner;


public class Pathman {
    static final int GRID_LENGTH = 100;
    static final int GRID_WIDTH = 100;
    static final int INIT_POS_X = 50;
    static final int INIT_POS_Y = 50;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean[][] passedPositions = new boolean[GRID_LENGTH][GRID_WIDTH];
        int xPosition = INIT_POS_X - 1, yPosition = INIT_POS_Y - 1;

        int commands = in.nextInt();
        for (int i = 0; i < commands; i++) {
            int xCommand = in.nextInt();
            int yCommand = in.nextInt();
            if (isValid(xPosition, yPosition, xCommand, yCommand, passedPositions)) {
                markPath(xPosition, yPosition, xCommand, yCommand, passedPositions);
                xPosition += xCommand;
                yPosition += yCommand;
            }
        }
        
        System.out.printf("%d %d", xPosition + 1, yPosition + 1);
        in.close();
    }

    public static boolean isValid(
            int xPosition, int yPosition,
            int xCommand, int yCommand,
            boolean[][] passedPositions) {
        // border check
        if (xPosition + xCommand < 0
                || xPosition + xCommand >= passedPositions.length
                || yPosition + yCommand < 0
                || yPosition + yCommand >= passedPositions[0].length)
            return false;

        // overlap check
        if (xCommand > 0) {
            for (int i = 1; i <= xCommand; i++)
                if (passedPositions[xPosition + i][yPosition])
                    return false;
        } else {
            for (int i = -1; i >= xCommand; i--)
                if (passedPositions[xPosition + i][yPosition])
                    return false;
        }
        if (yCommand > 0) {
            for (int i = 1; i <= yCommand; i++)
                if (passedPositions[xPosition + xCommand][yPosition + i])
                    return false;
        } else {
            for (int i = -1; i >= yCommand; i--)
                if (passedPositions[xPosition + xCommand][yPosition + i])
                    return false;
        }
        return true;
    }

    public static void markPath(
            int xPosition, int yPosition,
            int xCommand, int yCommand,
            boolean[][] passedPositions) {
        if (xCommand > 0) {
            for (int i = 0; i <= xCommand; i++)
                passedPositions[xPosition + i][yPosition] = true;
        } else {
            for (int i = 0; i >= xCommand; i--)
                passedPositions[xPosition + i][yPosition] = true;
        }
        if (yCommand > 0) {
            for (int i = 0; i <= yCommand; i++)
                passedPositions[xPosition + xCommand][yPosition + i] = true;
        } else {
            for (int i = 0; i >= yCommand; i--)
                passedPositions[xPosition + xCommand][yPosition + i] = true;
        }
    }
}