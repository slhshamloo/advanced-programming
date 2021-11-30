package game.board;

import game.AttackState;
import game.fleet.Ship;

public class BoardSquare {
    private final Ship ship;
    private Marker marker;
    public BoardSquare() {
        this(Marker.EMPTY);
    }

    public BoardSquare(Marker marker) {
        this(marker, null);
    }

    public BoardSquare(Ship ship) {
        this(Marker.SHIP, ship);
    }

    public BoardSquare(Marker marker, Ship ship) {
        this.marker = marker;
        this.ship = ship;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Ship getShip() {
        return ship;
    }

    public AttackState attack() {
        if (marker == Marker.SHIP || marker == Marker.INVISIBLE) {
            marker = Marker.DESTROYED;
            ship.damage();
            if (ship.getHealth() > 0)
                return AttackState.DESTROY;
            else
                return AttackState.ELIMINATE;
        } else if (marker == Marker.MINE) {
            marker = Marker.EXPLODED;
            return AttackState.EXPLODE;
        } else if (marker != Marker.DESTROYED && marker != Marker.EXPLODED)
            marker = Marker.MISSED;
        return AttackState.MISS;
    }

    public String scan() {
        if (marker == Marker.SHIP)
            return Marker.SHIP_UNKNOWN.label;
        else
            return Marker.EMPTY.label;
    }

    public String getOpponentView() {
        if (marker == Marker.MISSED || marker == Marker.EXPLODED) {
            return marker.label;
        } else if (marker == Marker.DESTROYED) {
            if (this.ship.getHealth() > 0)
                return Marker.DESTROYED_UNKNOWN.label;
            else
                return marker.label + ship.getLength();
        } else
            return Marker.EMPTY.label;
    }

    @Override
    public String toString() {
        if (marker == Marker.SHIP
                || marker == Marker.DESTROYED
                || marker == Marker.INVISIBLE)
            return marker.label + ship.getLength();
        return marker.label;
    }

    public enum Marker {
        EMPTY("  "),
        SHIP("S"), SHIP_UNKNOWN("SX"),
        DESTROYED("D"), DESTROYED_UNKNOWN("DX"),
        INVISIBLE("I"),
        MISSED("XX"),
        MINE("Mm"), EXPLODED("MX"),
        ANTIAIR("AA");

        private final String label;

        Marker(String label) {
            this.label = label;
        }
    }
}
