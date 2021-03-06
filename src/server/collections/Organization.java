package server.collections;

import java.time.ZonedDateTime;

import server.Wrapperable;
import server.annotations.Autogenerated;
import server.annotations.Larger;
import server.annotations.NotNull;
import server.annotations.Nullable;
import server.exceptions.CollectionsException;

public class Organization extends Entity implements Wrapperable {
    @Autogenerated
    @Larger(value=0)
    private long id;
    @NotNull
    private String name;
    @NotNull
    private Coordinates coordinates;
    @Autogenerated
    @NotNull
    private ZonedDateTime creationDate;
    @Larger(value=0)
    private double annualTurnover;
    @Larger(value=0)
    private long employeesCount;
    @Nullable
    private OrganizationType type;
    @Nullable
    private Address postalAddress;

    public Organization() {}

    public Organization(long id) {
        if (id <= 0L) {
            throw new CollectionsException("id должно быть больше нуля");
        }
        this.id = id;
        this.creationDate = ZonedDateTime.now();
    }

    @Override
    public String toString() {
        return "Учреждение";
    }

    @Override
    public String getInfo() {
        return String.format("||Учреждение с id %s||\nНазвание - %s\nКоординаты - %s\nВремя создания - %s\n" +
                "Годовой оборот - %s\nКоличество персонала - %s\nТип организации - %s\nАдрес - %s",
                this.id,
                this.name,
                this.coordinates.getInfo(),
                this.creationDate,
                this.annualTurnover,
                this.employeesCount,
                (this.type != null)? this.type.toString() : "?",
                (this.postalAddress != null)? this.postalAddress.getInfo() : "?");
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public double getAnnualTurnover() {
        return this.annualTurnover;
    }

    public long getEmployeesCount() {
        return this.employeesCount;
    }

    public OrganizationType getType() {
        return this.type;
    }

    public Address getPostalAddress() {
        return this.postalAddress;
    }

    public double countWeight() {
        return annualTurnover + employeesCount;
    }
}
