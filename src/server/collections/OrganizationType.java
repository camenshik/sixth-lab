package server.collections;

public enum OrganizationType {
    COMMERCIAL("Коммерческое"),
    PUBLIC("Общественное"),
    OPEN_JOINT_STOCK_COMPANY("ОАО");

    private final String type;
    OrganizationType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
