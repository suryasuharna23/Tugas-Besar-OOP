
//package spakborhills.entity;
//
//import spakborhills.enums.Location;
//import spakborhills.enums.Weather;
//import spakborhills.enums.ItemType;
//import spakborhills.enums.Season;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Arrays;
//import java.util.random.RandomGenerator;
//import java.util.random.RandomGeneratorFactory;
//
//
//public class ItemFactory {
//    public static Items createItem(String itemName) {
//        return switch (itemName.toLowerCase()) {
//            case "parsnip seeds" ->
//                new Seed("Parsnip Seeds", 20, 10, false, ItemType.SEEDS, 1, 1, Season.SPRING, Weather.RAINY);
//            case "bullhead" ->
//                new Fish("Bullhead", true, ItemType.FISH, "common", Arrays.asList(Season.values()), Arrays.asList(Weather.values()), Arrays.asList(Location.values()), 1);
//            default ->
//                throw new IllegalArgumentException("Invalid item name: " + itemName);
//        };
//    }
//}
//=======
//package spakborhills.entity;
//
//import spakborhills.enums.Location;
//import spakborhills.enums.Weather;
//import spakborhills.enums.ItemType;
//import spakborhills.enums.Season;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Arrays;
//import java.util.random.RandomGenerator;
//import java.util.random.RandomGeneratorFactory;
//
//
//public class ItemFactory {
//    public static Items createItem(String itemName) {
//        return switch (itemName.toLowerCase()) {
//            case "parsnip seeds" ->
//                new Seed("Parsnip Seeds", 20, 10, false, ItemType.SEEDS, 1, 1, Season.SPRING, Weather.RAINY);
//            case "bullhead" ->
//                new Fish("Bullhead", true, ItemType.FISH, "common", Arrays.asList(Season.values()), Arrays.asList(Weather.values()), Arrays.asList(Location.values()), 1);
//            default ->
//                throw new IllegalArgumentException("Invalid item name: " + itemName);
//        };
//    }
//}
