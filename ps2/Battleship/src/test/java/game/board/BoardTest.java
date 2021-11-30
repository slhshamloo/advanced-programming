package game.board;

import game.fleet.Direction;
import game.fleet.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private final Board testBoard = new Board(10, 10);

    @Test
    void isCoordinatesValid() {
        assertFalse(testBoard.isCoordinatesInvalid(5, 5));
        assertTrue(testBoard.isCoordinatesInvalid(11, 5));
        assertTrue(testBoard.isCoordinatesInvalid(-1, 5));
        assertTrue(testBoard.isCoordinatesInvalid(5, -1));
        assertTrue(testBoard.isCoordinatesInvalid(5, 11));
    }

    @Test
    void isShipInsideBorders() {
        Ship horizontalShip = new Ship(2, Direction.HORIZONTAL);
        Ship verticalShip = new Ship(2, Direction.VERTICAL);
        Ship horizontalPointShip = new Ship(1, Direction.HORIZONTAL);
        Ship verticalPointShip = new Ship(1, Direction.VERTICAL);

        assertTrue(testBoard.isShipOffTheBoard(horizontalShip, 10, 5));
        assertTrue(testBoard.isShipOffTheBoard(verticalShip, 5, 10));
        assertFalse(testBoard.isShipOffTheBoard(horizontalPointShip, 10, 5));
        assertFalse(testBoard.isShipOffTheBoard(verticalPointShip, 5, 10));
        assertFalse(testBoard.isShipOffTheBoard(horizontalShip, 9, 5));
        assertFalse(testBoard.isShipOffTheBoard(verticalShip, 5, 9));
    }
}