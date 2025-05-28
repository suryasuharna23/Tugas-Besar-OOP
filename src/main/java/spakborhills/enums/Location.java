package spakborhills.enums;

public enum Location {
    ABIGAILS_HOUSE("Abigail's House"),
    CAROLINES_HOUSE("Caroline's House"),
    DASCOS_HOUSE("Dasco's House"),
    MAYOR_TADIS_HOUSE("Mayor Tadi's House"),
    PERRYS_HOUSE("Perry's House"),
    STORE("Store"),
    FARM("Farm"),
    FOREST_RIVER("Forest River"),
    MOUNTAIN_LAKE("Mountain Lake"),
    OCEAN("Ocean"),
    PLAYERS_HOUSE("Player's House"),
    UNKNOWN("Unknown");

    private final String displayName;

    Location(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Location fromMapName(String mapName) {
        if (mapName == null)
            return UNKNOWN;

        switch (mapName.toLowerCase().trim()) {
            case "abigail's house":
                return ABIGAILS_HOUSE;
            case "caroline's house":
                return CAROLINES_HOUSE;
            case "dasco's house":
                return DASCOS_HOUSE;
            case "mayor tadi's house":
                return MAYOR_TADIS_HOUSE;
            case "perry's house":
                return PERRYS_HOUSE;
            case "store":
                return STORE;
            case "farm":
                return FARM;
            case "forest river":
                return FOREST_RIVER;
            case "mountain lake":
                return MOUNTAIN_LAKE;
            case "ocean":
                return OCEAN;
            case "player's house":
                return PLAYERS_HOUSE;
            default:
                return UNKNOWN;
        }
    }
}