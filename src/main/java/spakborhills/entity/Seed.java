//package spakborhills.entity;
//
//
//import spakborhills.enums.ItemType;
//import spakborhills.enums.Location;
//import spakborhills.enums.Season;
//import spakborhills.enums.Weather;
//import spakborhills.interfaces.Usable;
//
//public class Seed extends Items implements Usable {
//    private int dayToHarvest;
//    private int countWater;
//    private Season season;
//    private Weather weather;
//    private int buyPrice;
//    private int sellPrice;
//
//    public Seed(String name, int buyPrice, int sellPrice, boolean isEdible, ItemType type, int countWater, int dayToHarvest, Season season, Weather weather) {
//        super(name, isEdible, ItemType.SEEDS);
//        this.season  = season;
//        this.weather = weather;
//        this.dayToHarvest = dayToHarvest;
//        this.buyPrice = buyPrice;
//        this.sellPrice = sellPrice;
//        this.countWater = countWater;
//    }
//
//    public int getCountWater() {
//        return countWater;
//    }
//
//    public void setCountWater(int countWater) {
//        if (weather == Weather.SUNNY) {
//            this.countWater++;
//            System.out.println("Water count: " + this.countWater);
//        }
//    }
//    public Season getSeason() {
//        return season;
//    }
//    public Weather getWeather(){
//        return weather;
//    }
//
//    public void setWeather(Weather weather) {
//        this.weather = weather;
//    }
//
//    public int getDayToHarvest() { return dayToHarvest; }
//
//    public void use(Player player) {
//        player.plantSeed(this);
//        player.getEquippedItem(); //ini yg dikasih di method remove tuh indexnya atau jumlahnya
//    }
//
//
//
//}