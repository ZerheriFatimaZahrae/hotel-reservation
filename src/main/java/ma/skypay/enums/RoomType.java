package ma.skypay.enums;

public enum RoomType {
    STANDARD("standard"),
    JUNIOR_SUITE("junior"),
    MASTER_SUITE("suite");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

    public static RoomType fromDisplayName(String displayName) {
        for (RoomType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type: " + displayName);
    }
}
