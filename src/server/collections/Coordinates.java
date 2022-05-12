package server.collections;

import server.annotations.Less;

public class Coordinates extends SubEntity {
    @Less(676)
    private double x;
    @Less(514)
    private double y;

    public Coordinates() {}

    @Override
    public String toString() {
        return "Координаты";
    }

    @Override
    public String getInfo() {
        return String.format("x: %s; y: %s",
                (this.x != 0.0)? this.x : "?",
                (this.y != 0.0)? this.y : "?"
        );
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
