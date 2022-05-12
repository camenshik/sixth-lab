package server.collections;

import server.annotations.NotNull;

public class Address extends SubEntity {
    @NotNull
    private String street;
    @NotNull
    private Location town;

    public Address() {}

    @Override
    public String toString() {
        return "Адрес";
    }

    @Override
    public String getInfo() {
        return String.format("Город - %s\nУлица - %s",
                (this.town != null)? this.town.getInfo() : "?",
                (this.street != null)? this.street : "?"
        );
    }

    public String getStreet() {
        return this.street;
    }

    public Location getTown() {
        return this.town;
    }
}
