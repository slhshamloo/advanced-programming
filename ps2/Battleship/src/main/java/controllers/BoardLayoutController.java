package controllers;

import game.Item;
import game.Player;
import game.fleet.Direction;
import game.fleet.Ship;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardLayoutController extends AbstractController {
    private static final int HORIZONTAL_ANTIAIR_WIDTH = 3;
    private static final int VERTICAL_ANTIAIR_WIDTH = 3;
    private final Player player;
    private final int[] shipCounts;

    public BoardLayoutController(Scanner inputStream, Player player, int[] shipCounts) {
        super(inputStream, "finish-arranging");
        this.player = player;
        this.shipCounts = shipCounts.clone();
    }

    private static void printHelp() {
        System.out.println("put S[number] [x],[y] [-h|-v]\n" +
                "put-mine [x],[y]\n" +
                "put-antiaircraft [s] [-h|-v]\n" +
                "invisible [x],[y]\n" +
                "show-my-board\n" +
                "help\n" +
                "finish-arranging");
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("put S([0-9]+) (-?[0-9]+),(-?[0-9]+) (-\\S)"),
                this::placeShip);
        commandMap.put(Pattern.compile("put-mine ([0-9]+),([0-9]+)"), this::placeMine);
        commandMap.put(Pattern.compile("put-antiaircraft ([0-9]+) (-\\S)"), this::placeAntiair);
        commandMap.put(Pattern.compile("invisible (-?[0-9]+),(-?[0-9]+)"), this::makeInvisible);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("help", BoardLayoutController::printHelp);
        noArgumentCommandMap.put("show-my-board", this::showBoard);

        return noArgumentCommandMap;
    }

    private void placeShip(Matcher matcher) {
        int shipLength = Integer.parseInt(matcher.group(1));
        int xCoordinate = Integer.parseInt(matcher.group(2));
        int yCoordinate = Integer.parseInt(matcher.group(3));
        String directionString = matcher.group(4);
        Direction direction = Direction.getDirectionFromString(directionString);

        if (shipLength < 1 || shipLength > 4)
            System.out.println("invalid ship number");
        else if (player.getBoard().isCoordinatesInvalid(xCoordinate, yCoordinate))
            System.out.println("wrong coordination");
        else if (direction == null)
            System.out.println("invalid direction");
        else {
            Ship ship = new Ship(shipLength, direction);

            if (player.getBoard().isShipOffTheBoard(ship, xCoordinate, yCoordinate))
                System.out.println("off the board");
            else if (shipCounts[shipLength - 1] <= 0)
                System.out.println("you don't have this type of ship");
            else if (player.getBoard().hasOverlapWithPlacedShipOrMine(
                    ship, xCoordinate, yCoordinate))
                System.out.println("collision with the other ship or mine on the board");
            else {
                player.getBoard().placeShip(ship, xCoordinate, yCoordinate);
                shipCounts[shipLength - 1]--;
                player.addShip();
            }
        }
    }

    private void placeMine(Matcher matcher) {
        int xCoordinate = Integer.parseInt(matcher.group(1));
        int yCoordinate = Integer.parseInt(matcher.group(2));

        if (player.getBoard().isCoordinatesInvalid(xCoordinate, yCoordinate))
            System.out.println("wrong coordination");
        else if (player.getUser().getItemAmount(Item.MINE) <= 0)
            System.out.println("you don't have enough mine");
        else if (player.getBoard().isShipOrMine(xCoordinate, yCoordinate))
            System.out.println("collision with the other ship or mine on the board");
        else {
            player.getBoard().placeMine(xCoordinate, yCoordinate);
            player.getUser().useItem(Item.MINE);
        }
    }

    private void placeAntiair(Matcher matcher) {
        int startRow = Integer.parseInt(matcher.group(1));
        String directionString = matcher.group(2);
        Direction direction = Direction.getDirectionFromString(directionString);

        if (player.getBoard().isCoordinatesInvalid(startRow, startRow))
            System.out.println("wrong coordination");
        else if (player.getBoard().isAntiairOffTheBoard(
                startRow, HORIZONTAL_ANTIAIR_WIDTH, Direction.HORIZONTAL)
                || player.getBoard().isAntiairOffTheBoard(
                startRow, VERTICAL_ANTIAIR_WIDTH, Direction.VERTICAL))
            System.out.println("off the board");
        else if (direction == null)
            System.out.println("invalid direction");
        else if (player.getUser().getItemAmount(Item.ANTIAIR) <= 0)
            System.out.println("you don't have enough antiaircraft");
        else {
            if (direction == Direction.HORIZONTAL)
                player.getBoard().placeAntiair(
                        startRow, HORIZONTAL_ANTIAIR_WIDTH, Direction.HORIZONTAL);
            else if (direction == Direction.VERTICAL)
                player.getBoard().placeAntiair(
                        startRow, VERTICAL_ANTIAIR_WIDTH, Direction.VERTICAL);
            player.getUser().useItem(Item.ANTIAIR);
        }
    }

    private void makeInvisible(Matcher matcher) {
        int xCoordinate = Integer.parseInt(matcher.group(1));
        int yCoordinate = Integer.parseInt(matcher.group(2));

        if (player.getBoard().isCoordinatesInvalid(xCoordinate, yCoordinate))
            System.out.println("wrong coordination");
        else if (player.getUser().getItemAmount(Item.INVISIBLE) <= 0)
            System.out.println("you don't have enough invisible");
        else if (!player.getBoard().isShip(xCoordinate, yCoordinate))
            System.out.println("there is no ship on this place on the board");
        else if (player.getBoard().isInvisible(xCoordinate, yCoordinate))
            System.out.println("this place has already made invisible");
        else {
            player.getBoard().makeInvisible(xCoordinate, yCoordinate);
            player.getUser().useItem(Item.INVISIBLE);
        }
    }

    private void showBoard() {
        System.out.println(player.getBoard().toString());
    }

    private boolean isAnyShipRemaining() {
        for (int shipCount : shipCounts)
            if (shipCount > 0)
                return true;
        return false;
    }

    @Override
    protected void escape() {
        if (isAnyShipRemaining()) {
            System.out.println("you must put all ships on the board");
            this.run();
        } else
            System.out.println("turn completed");
    }
}
