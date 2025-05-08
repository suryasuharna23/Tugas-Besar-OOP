package spakborhills.models;

public class Farm {
    private String farmName;
    private Player player;
    private final FarmMap farmMap;
    
    public Farm(String farmName, Player player, FarmMap farmMap){
        this.farmName = farmName;
        this.player = player;
        this.farmMap = farmMap;
    }

    public String getFarmName(){
        return farmName;
    }

    public void setFarmName(String farmName){
        this.farmName = farmName;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public FarmMap getFarmMap(){
        return farmMap;
    }


}
