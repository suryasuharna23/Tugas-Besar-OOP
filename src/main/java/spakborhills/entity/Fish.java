//
//package spakborhills.entity;
//
//import spakborhills.enums.ItemType;
//import spakborhills.enums.Location;
//import spakborhills.enums.Season;
//import spakborhills.enums.Weather;
//import spakborhills.interfaces.Edible;
//
//import java.sql.Time;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Fish extends Items implements Edible {
//    private String name;
//    private String fishType;
//    private List<Season> seasons = new ArrayList<Season>();
//    private List<Weather> weathers = new ArrayList<Weather>();
//    private List<Location> locations = new ArrayList<Location>();
//    private List<Time> times = new ArrayList<Time>();
//    private int startTime;
//    private int endTime;
//    private int buyPrice = 0;
//    private int additionalEnergy;
//
//    public Fish(String name, boolean isEdible, ItemType type, String fishType, List<Season> seasons, List<Weather> weathers, List<Location> locations, int additionalEnergy) {
//        super(name, isEdible, type);
//        this.name = name;
//        this.fishType = fishType;
//        this.seasons = seasons;
//        this.weathers = weathers;
//        this.locations = locations;
//        this.buyPrice = buyPrice;
//        this.additionalEnergy = additionalEnergy;
//        this.buyPrice = buyPrice;
//    }
//
//    public int restoredEnergy(int additionalEnergy) {
//        return this.additionalEnergy;
//    }
//
//    public int multiplier() {
//        int mult = 0;
//        if ((this.fishType.toLowerCase()).equals("common")) {
//            mult = 10;
//        }
//        else if ((this.fishType.toLowerCase()).equals("regular")) {
//            mult =  5;
//        }
//        else if ((this.fishType.toLowerCase()).equals("legendary")) {
//            mult = 25;
//        }
//        return mult;
//    }
//
//    public int countSeasons() {
//        return this.seasons.size();
//    }
//
//    public int countWeathers() {
//        return this.weathers.size();
//    }
//
//    public int countLocation() {
//        return this.locations.size();
//    }
//
//    public int countTime() {
//        return this.times.size();
//    }
//
//    public void setBuyPrice() {
//        this.buyPrice = (4/this.countSeasons()) * (24/this.countTime()) * (2/this.countWeathers()) * (4/this.countTime()) * this.multiplier();
//        System.out.println("Buy price: " + this.buyPrice);
//    }
//
//    public void use(Player player) {
//        player.fishing(this);
//        //player.addItemToInventory(this);
//
//        if (player.eating(this)) {
//            player.currentEnergy += restoredEnergy(1);
//        }
//    }
//}
//>>>>>>> Stashed changes
