package spakborhills;

public class MapInfo {
    public String mapName;
    public String mapDataPath;
    public String mapLayoutPath;

    public MapInfo (String mapName, String mapDataPath, String mapLayoutPath){
        this.mapName = mapName;
        this.mapDataPath = mapDataPath;
        this.mapLayoutPath = mapLayoutPath;
    }

    public String getMapName(){
        return this.mapName;
    }

    public String getMapDataPath(){
        return this.mapDataPath;
    }

    public String getMapLayoutPath(){
        return this.mapLayoutPath;
    }


}
