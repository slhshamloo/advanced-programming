package game.board;

import game.AttackState;
import game.fleet.Antiair;
import game.fleet.Direction;
import game.fleet.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

public class Board {
    private final BoardSquare[][] boardSquares;
    private final List<Antiair> antiairs = new ArrayList<>();
    private final int xLength;
    private final int yLength;

    public Board(int xLength, int yLength) {
        this.xLength = xLength;
        this.yLength = yLength;
        boardSquares = new BoardSquare[yLength][xLength];
        this.fillWithEmptySquares();
    }

    private void fillWithEmptySquares() {
        for (int i = 0; i < yLength; i++)
            for (int j = 0; j < xLength; j++)
                boardSquares[i][j] = new BoardSquare();
    }

    public boolean isCoordinatesInvalid(int xCoordinate, int yCoordinate) {
        return xCoordinate <= 0
                || yCoordinate <= 0
                || xCoordinate > xLength
                || yCoordinate > yLength;
    }

    public boolean isShipOffTheBoard(Ship ship, int xCoordinate, int yCoordinate) {
        if (ship.getDirection() == Direction.HORIZONTAL)
            return xCoordinate - 1 + ship.getLength() > xLength;
        else if (ship.getDirection() == Direction.VERTICAL)
            return yCoordinate - 1 + ship.getLength() > yLength;
        return false;
    }

    public boolean isAntiairOffTheBoard(int startRow, int width, Direction direction) {
        if (direction == Direction.VERTICAL)
            return startRow - 1 + width > xLength;
        else if (direction == Direction.HORIZONTAL)
            return startRow - 1 + width > yLength;
        return false;
    }

    public boolean isAreaOffTheBoard(int xCoordinate, int yCoordinate,
            int xAreaLength, int yAreaLength) {
        return xCoordinate + xAreaLength - 1 > xLength
                || yCoordinate + yAreaLength - 1 > yLength;
    }

    public boolean isAntiairActivated(int xCoordinate, int yCoordinate,
            int xAttackLength, int yAttackLength) {
        for (Antiair antiair : antiairs)
            if (antiair.coversArea(xCoordinate, yCoordinate, xAttackLength, yAttackLength)) {
                removeAntiairMarkers(
                        antiair.getStartRow(), antiair.getWidth(), antiair.getDirection());
                antiairs.remove(antiair);
                // refill other antiairs' markers that got removed
                for (Antiair remainingAntiair : antiairs)
                    fillEmptySquaresWithAntiairMarker(
                            remainingAntiair.getStartRow(), remainingAntiair.getWidth(),
                            remainingAntiair.getDirection());
                return true;
            }
        return false;
    }

    public boolean isEmpty(int xCoordinate, int yCoordinate) {
        BoardSquare.Marker marker = boardSquares[yCoordinate - 1][xCoordinate - 1].getMarker();
        return marker == BoardSquare.Marker.EMPTY;
    }

    public boolean isShip(int xCoordinate, int yCoordinate) {
        BoardSquare.Marker marker = boardSquares[yCoordinate - 1][xCoordinate - 1].getMarker();
        return marker == BoardSquare.Marker.SHIP
                || marker == BoardSquare.Marker.INVISIBLE;
    }

    public boolean isMine(int xCoordinate, int yCoordinate) {
        BoardSquare.Marker marker = boardSquares[yCoordinate - 1][xCoordinate - 1].getMarker();
        return marker == BoardSquare.Marker.MINE;
    }

    public boolean isShipOrMine(int xCoordinate, int yCoordinate) {
        return isShip(xCoordinate, yCoordinate)
                || isMine(xCoordinate, yCoordinate);
    }

    public boolean isInvisible(int xCoordinate, int yCoordinate) {
        BoardSquare.Marker marker = boardSquares[yCoordinate - 1][xCoordinate - 1].getMarker();
        return marker == BoardSquare.Marker.INVISIBLE;
    }

    public boolean isAttacked(int xCoordinate, int yCoordinate) {
        BoardSquare.Marker marker = boardSquares[yCoordinate - 1][xCoordinate - 1].getMarker();
        return marker == BoardSquare.Marker.DESTROYED
                || marker == BoardSquare.Marker.EXPLODED
                || marker == BoardSquare.Marker.MISSED;
    }

    public boolean hasOverlapWithPlacedShipOrMine(Ship ship, int xCoordinate, int yCoordinate) {
        if (ship.getDirection() == Direction.HORIZONTAL) {
            for (int i = 0; i < ship.getLength(); i++)
                if (isShipOrMine(xCoordinate + i, yCoordinate))
                    return true;
        } else if (ship.getDirection() == Direction.VERTICAL) {
            for (int i = 0; i < ship.getLength(); i++)
                if (isShipOrMine(xCoordinate, yCoordinate + i))
                    return true;
        }
        return false;
    }

    public void placeShip(Ship ship, int xCoordinate, int yCoordinate) {
        if (ship.getDirection() == Direction.HORIZONTAL)
            for (int i = 0; i < ship.getLength(); i++)
                boardSquares[yCoordinate - 1][xCoordinate - 1 + i]
                        = new BoardSquare(ship);
        else if (ship.getDirection() == Direction.VERTICAL)
            for (int i = 0; i < ship.getLength(); i++)
                boardSquares[yCoordinate - 1 + i][xCoordinate - 1]
                        = new BoardSquare(ship);
    }

    public void placeMine(int xCoordinate, int yCoordinate) {
        boardSquares[yCoordinate - 1][xCoordinate - 1].setMarker(BoardSquare.Marker.MINE);
    }

    public void placeAntiair(int startRow, int width, Direction direction) {
        Antiair antiair = new Antiair(startRow, width, direction);
        antiairs.add(antiair);
        fillEmptySquaresWithAntiairMarker(startRow, width, direction);
    }

    private void fillEmptySquaresWithAntiairMarker(int startRow, int width, Direction direction) {
        if (direction == Direction.HORIZONTAL)
            for (int i = 0; i < width; i++)
                fillYRowWithAntiair(startRow + i);
        else if (direction == Direction.VERTICAL)
            for (int i = 0; i < width; i++)
                fillXRowWithAntiair(startRow + i);
    }

    private void fillXRowWithAntiair(int xRow) {
        for (int i = 1; i <= yLength; i++)
            if (isEmpty(xRow, i))
                boardSquares[i - 1][xRow - 1].setMarker(BoardSquare.Marker.ANTIAIR);
    }

    private void fillYRowWithAntiair(int yRow) {
        for (int i = 1; i <= xLength; i++)
            if (isEmpty(i, yRow))
                boardSquares[yRow - 1][i - 1].setMarker(BoardSquare.Marker.ANTIAIR);
    }

    private void removeAntiairMarkers(int startRow, int width, Direction direction) {
        if (direction == Direction.HORIZONTAL)
            for (int i = 0; i < width; i++)
                removeYRowAntiairMarkers(startRow + i);
        else if (direction == Direction.VERTICAL)
            for (int i = 0; i < width; i++)
                removeXRowAntiairMarkers(startRow + i);
    }

    private void removeXRowAntiairMarkers(int xRow) {
        for (int i = 1; i <= yLength; i++)
            if (boardSquares[i - 1][xRow - 1].getMarker() == BoardSquare.Marker.ANTIAIR)
                boardSquares[i - 1][xRow - 1].setMarker(BoardSquare.Marker.EMPTY);
    }

    private void removeYRowAntiairMarkers(int yRow) {
        for (int i = 1; i <= xLength; i++)
            if (boardSquares[yRow - 1][i - 1].getMarker() == BoardSquare.Marker.ANTIAIR)
                boardSquares[yRow - 1][i - 1].setMarker(BoardSquare.Marker.EMPTY);
    }

    public void makeInvisible(int xCoordinate, int yCoordinate) {
        boardSquares[yCoordinate - 1][xCoordinate - 1].setMarker(BoardSquare.Marker.INVISIBLE);
    }

    public AttackState attack(int xCoordinate, int yCoordinate) {
        return boardSquares[yCoordinate - 1][xCoordinate - 1].attack();
    }

    public int getShipLength(int xCoordinate, int yCoordinate) {
        if (boardSquares[yCoordinate - 1][xCoordinate - 1].getShip() != null)
            return boardSquares[yCoordinate - 1][xCoordinate - 1].getShip().getLength();
        else
            return -1;
    }

    private String getView(int xCoordinate, int yCoordinate, int xAreaLength, int yAreaLength,
            Function<BoardSquare, String> boardSquareView) {
        StringJoiner boardJoiner = new StringJoiner("|\n|", "|", "|");

        for (int i = 0; i < yAreaLength; i++) {
            StringJoiner rowJoiner = new StringJoiner("|");
            for (int j = 0; j < xAreaLength; j++)
                rowJoiner.add(boardSquareView.apply(
                        boardSquares[yCoordinate - 1 + i][xCoordinate - 1 + j]));
            boardJoiner.merge(rowJoiner);
        }

        return boardJoiner.toString();
    }

    public String scan(int xCoordinate, int yCoordinate, int xAreaLength, int yAreaLength) {
        return getView(xCoordinate, yCoordinate, xAreaLength, yAreaLength, BoardSquare::scan);
    }

    public String getOpponentView() {
        return getView(1, 1, xLength, yLength, BoardSquare::getOpponentView);
    }

    @Override
    public String toString() {
        return getView(1, 1, xLength, yLength, BoardSquare::toString);
    }
}
