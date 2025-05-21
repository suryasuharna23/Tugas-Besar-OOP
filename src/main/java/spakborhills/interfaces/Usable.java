//package spakborhills.entity;
package spakborhills.interfaces;


import spakborhills.enums.Location;
import spakborhills.enums.Season;
import spakborhills.enums.Weather;

import java.sql.Time;

public interface Usable {
    public Season getSeason();
    public Weather getWeather();
    //public Time rangeTime (Time start, Time end);
   // public Location getLocation();
}

