package server.collections;

import server.annotations.NotNull;

public class Location extends SubEntity {
    private double x;
    @NotNull
    private Float y;
    @NotNull
    private Double z;
    @NotNull
    private String name;

    public Location() {}

    public String toString() {
        return "Местоположение";
    }

    @Override
    public String getInfo() {
        return String.format("%s, x: %s y: %s z: %s",
                (this.name != null)? this.name : "?",
                (this.x != 0.0)? this.x : "?",
                (this.y != null)? this.y : "?",
                (this.z != null)? this.z : "?"
        );
    }

    public double getX() {
        return this.x;
    }

    public Float getY() {
        return this.y;
    }

    public Double getZ() {
        return this.z;
    }

    public String getName() {
        return this.name;
    }
}
