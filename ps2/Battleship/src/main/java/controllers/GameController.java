package controllers;

import game.AttackState;
import game.Item;
import game.Player;
import game.board.Board;
import game.fleet.Direction;
import user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController extends AbstractController {
    private static final int BOARD_X_LENGTH = 10;
    private static final int BOARD_Y_LENGTH = 10;
    private static final int HORIZONTAL_AIRPLANE_ATTACK_LENGTH = 5;
    private static final int HORIZONTAL_AIRPLANE_ATTACK_WIDTH = 2;
    private static final int VERTICAL_AIRPLANE_ATTACK_LENGTH = 5;
    private static final int VERTICAL_AIRPLANE_ATTACK_WIDTH = 2;
    private static final int SCAN_X_LENGTH = 3;
    private static final int SCAN_Y_LENGTH = 3;
    private static final int WIN_REWARD = 50;
    private static final int DRAW_REWARD = 25;
    private static final int FORFEIT_PUNISHMENT = 50;
    private Player attacker;
    private Player defender;

    public GameController(Scanner inputStream, User playerOne, User playerTwo) {
        super(inputStream);
        setEscapeCondition(this::isGameEnded);

        Board attackerBoard = new Board(BOARD_X_LENGTH, BOARD_Y_LENGTH);
        Board defenderBoard = new Board(BOARD_X_LENGTH, BOARD_Y_LENGTH);

        this.attacker = new Player(playerOne, attackerBoard);
        this.defender = new Player(playerTwo, defenderBoard);
    }

    private static void printHelp() {
        System.out.println("bomb [x],[y]\n" +
                "put-airplane [x],[y] [-h|-v]\n" +
                "scanner [x],[y]\n" +
                "show-turn\n" +
                "show-my-board\n" +
                "show-rival-board\n" +
                "help\n" +
                "forfeit");
    }

    private boolean isGameEnded(String input) {
        return input.equals("forfeit")
                || defender.getShipCount() <= 0
                || attacker.getShipCount() <= 0;
    }

    @Override
    public void run() {
        int[] shipCounts = {4, 3, 2, 1};

        Controller boardLayoutController = new BoardLayoutController(
                inputStream, attacker, shipCounts);
        boardLayoutController.run();

        boardLayoutController = new BoardLayoutController(inputStream, defender, shipCounts);
        boardLayoutController.run();

        super.run();
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("bomb (-?[0-9]+),(-?[0-9]+)"), this::attack);
        commandMap.put(Pattern.compile("put-airplane (-?[0-9]+),(-?[0-9]+) (-\\S)"),
                this::putAirplane);
        commandMap.put(Pattern.compile("scanner (-?[0-9]+),(-?[0-9]+)"), this::scan);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("show-turn", this::showTurn);
        noArgumentCommandMap.put("show-my-board", this::showAttackerBoard);
        noArgumentCommandMap.put("show-rival-board", this::showDefenderBoard);
        noArgumentCommandMap.put("help", GameController::printHelp);

        return noArgumentCommandMap;
    }

    private void endTurn() {
        swapPlayers();
        System.out.println("turn completed");
    }

    private void swapPlayers() {
        Player tempPlayer = attacker;
        attacker = defender;
        defender = tempPlayer;
    }

    private void attack(Matcher matcher) {
        int xCoordinate = Integer.parseInt(matcher.group(1));
        int yCoordinate = Integer.parseInt(matcher.group(2));

        if (defender.getBoard().isCoordinatesInvalid(xCoordinate, yCoordinate))
            System.out.println("wrong coordination");
        else if (defender.getBoard().isAttacked(xCoordinate, yCoordinate)) {
            System.out.println("this place has already destroyed");
        } else {
            AttackState attackState = attackAndAddPoints(xCoordinate, yCoordinate);

            if (attackState == AttackState.DESTROY)
                System.out.println("the rival's ship was damaged");
            else if (attackState == AttackState.ELIMINATE)
                System.out.println("the rival's ship"
                        + defender.getBoard().getShipLength(xCoordinate, yCoordinate)
                        + " was destroyed");
            else if (attackState == AttackState.EXPLODE) {
                System.out.println("you destroyed the rival's mine");
                endTurn();
                attackAndAddPoints(xCoordinate, yCoordinate);
            } else if (attackState == AttackState.MISS) {
                System.out.println("the bomb fell into sea");
                endTurn();
            }
        }
    }

    private AttackState attackAndAddPoints(int xCoordinate, int yCoordinate) {
        AttackState attackState = defender.getBoard().attack(xCoordinate, yCoordinate);

        if (attackState == AttackState.ELIMINATE) {
            defender.removeShip();
            attacker.increasePoints(1);
        } else if (attackState == AttackState.DESTROY)
            attacker.increasePoints(1);
        else if (attackState == AttackState.EXPLODE)
            attacker.decreasePoints(1);

        return attackState;
    }

    private void putAirplane(Matcher matcher) {
        int xCoordinate = Integer.parseInt(matcher.group(1));
        int yCoordinate = Integer.parseInt(matcher.group(2));
        String directionString = matcher.group(3);
        Direction direction = Direction.getDirectionFromString(directionString);

        if (defender.getBoard().isCoordinatesInvalid(xCoordinate, yCoordinate))
            System.out.println("wrong coordination");
        else if (direction == null)
            System.out.println("invalid direction");
        else {
            if (direction == Direction.HORIZONTAL)
                doAirstrike(xCoordinate, yCoordinate,
                        HORIZONTAL_AIRPLANE_ATTACK_LENGTH, HORIZONTAL_AIRPLANE_ATTACK_WIDTH);
            else if (direction == Direction.VERTICAL)
                doAirstrike(xCoordinate, yCoordinate,
                        VERTICAL_AIRPLANE_ATTACK_WIDTH, VERTICAL_AIRPLANE_ATTACK_LENGTH);
        }
    }

    private void doAirstrike(int xCoordinate, int yCoordinate,
            int xAttackLength, int yAttackLength) {
        if (defender.getBoard().isAreaOffTheBoard(xCoordinate, yCoordinate,
                xAttackLength, yAttackLength))
            System.out.println("off the board");
        else if (attacker.getUser().getItemAmount(Item.AIRPLANE) <= 0)
            System.out.println("you don't have airplane");
        else {
            attacker.getUser().useItem(Item.AIRPLANE);
            if (defender.getBoard().isAntiairActivated(xCoordinate, yCoordinate,
                    xAttackLength, yAttackLength))
                System.out.println("the rival's antiaircraft destroyed your airplane");
            else {
                int destroyedSquares = attackArea(xCoordinate, yCoordinate,
                        xAttackLength, yAttackLength);
                if (destroyedSquares > 0)
                    System.out.println(destroyedSquares + " pieces of rival's ships was damaged");
                else
                    System.out.println("target not found");
            }
        }
    }

    private int attackArea(int xCoordinate, int yCoordinate, int xAttackLength, int yAttackLength) {
        int destroyedSquares = 0;

        for (int i = 0; i < xAttackLength; i++)
            for (int j = 0; j < yAttackLength; j++) {
                if (defender.getBoard().isAttacked(xCoordinate + i, yCoordinate + j))
                    continue;

                AttackState squareAttackState = attackAndAddPoints(
                        xCoordinate + i, yCoordinate + j);

                if (squareAttackState == AttackState.DESTROY
                        || squareAttackState == AttackState.ELIMINATE)
                    destroyedSquares++;
                else if (squareAttackState == AttackState.EXPLODE) {
                    swapPlayers();
                    attackAndAddPoints(xCoordinate + i, yCoordinate + j);
                    swapPlayers();
                }
            }
        return destroyedSquares;
    }

    private void scan(Matcher matcher) {
        int xCoordinate = Integer.parseInt(matcher.group(1));
        int yCoordinate = Integer.parseInt(matcher.group(2));

        if (defender.getBoard().isCoordinatesInvalid(xCoordinate, yCoordinate))
            System.out.println("wrong coordination");
        else if (defender.getBoard().isAreaOffTheBoard(xCoordinate, yCoordinate,
                SCAN_X_LENGTH, SCAN_Y_LENGTH))
            System.out.println("off the board");
        else if (attacker.getUser().getItemAmount(Item.SCAN) <= 0)
            System.out.println("you don't have scanner");
        else {
            attacker.getUser().useItem(Item.SCAN);
            System.out.println(defender.getBoard().scan(xCoordinate, yCoordinate,
                    SCAN_X_LENGTH, SCAN_Y_LENGTH));
        }
    }

    private void showTurn() {
        System.out.println(attacker.getUser().getUsername() + "'s turn");
    }

    private void showAttackerBoard() {
        System.out.println(attacker.getBoard());
    }

    private void showDefenderBoard() {
        System.out.println(defender.getBoard().getOpponentView());
    }

    @Override
    protected void escape() {
        if (defender.getShipCount() <= 0) {
            submitResults(attacker, defender, false, attacker.getShipCount() <= 0);
        } else
            submitResults(defender, attacker, attacker.getShipCount() > 0, false);
    }

    private void submitResults(Player winnerOrDrawn, Player looserOrDrawn,
            boolean isGameConceded, boolean isGameDrawn) {
        if (isGameDrawn) {
            System.out.println("draw");

            winnerOrDrawn.getUser().submitDraw();
            looserOrDrawn.getUser().submitDraw();

            winnerOrDrawn.getUser().increaseCoins(DRAW_REWARD + winnerOrDrawn.getPoints());
            looserOrDrawn.getUser().increaseCoins(DRAW_REWARD + looserOrDrawn.getPoints());
        } else {
            if (isGameConceded) {
                System.out.println(looserOrDrawn.getUser().getUsername() + " is forfeited\n"
                        + winnerOrDrawn.getUser().getUsername() + " is winner");

                winnerOrDrawn.getUser().increaseCoins(winnerOrDrawn.getPoints());
                looserOrDrawn.getUser().decreaseCoins(FORFEIT_PUNISHMENT);
            } else {
                System.out.println(winnerOrDrawn.getUser().getUsername() + " is winner");

                winnerOrDrawn.getUser().increaseCoins(WIN_REWARD + winnerOrDrawn.getPoints());
                looserOrDrawn.getUser().increaseCoins(looserOrDrawn.getPoints());
            }

            winnerOrDrawn.getUser().submitWin(isGameConceded);
            looserOrDrawn.getUser().submitLoss(isGameConceded);
        }
    }
}
