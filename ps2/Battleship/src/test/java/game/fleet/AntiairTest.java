package game.fleet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AntiairTest {

    @Test
    void coversArea() {
        Antiair antiair = new Antiair(3, 3, Direction.VERTICAL);
        assertTrue(antiair.coversArea(4, 2, 5, 2));
    }
}