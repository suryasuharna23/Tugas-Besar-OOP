package spakborhills.models;

import spakborhills.enums.Location;
import spakborhills.enums.RelationshipStatus;
public class NPC {
    private String name;
    private Location npcLocation;
    private int heartPoints;
    private Items[] lovedItems;
    private Items[] hatedItems;
    private Items[] neutralItems;
    private RelationshipStatus relationshipStatus;

    public NPC(String name, Location npcLocation, int heartPoints, Items[] 
    lovedItems, Items[] hatedItems, Items[] neutralItems, RelationshipStatus relationshipStatus){
        this.name = name;
        this.npcLocation = npcLocation;
        this.heartPoints = heartPoints;
        this.lovedItems = lovedItems;
        this.hatedItems = hatedItems;
        this.neutralItems = neutralItems;
        this.relationshipStatus = relationshipStatus;
    }

    public String getNPCName(){
        return name;
    }

    public void setNPCName(String name){
        this.name = name;
    }

    public Location getNPCLocation(){
        return npcLocation;
    }

    public void setNPCLocation(Location npcLocation){
        this.npcLocation = npcLocation;
    }

    public int getNPCHeartPoints(){
        return heartPoints;
    }

    public void setNPCHeartPoints(int heartPoints){
        this.heartPoints = heartPoints;
    }

    public Items[] getNPCLovedItems(){
        return lovedItems;
    }

    public Items[] getNPCHatedItems(){
        return hatedItems;
    }

    public Items[] getNPCNeutralItems(){
        return neutralItems;
    }

    public RelationshipStatus getRelationshipStatus(){
        return relationshipStatus;
    }

    public void setRelationshipStatus(RelationshipStatus relationshipStatus){
        this.relationshipStatus = relationshipStatus;
    }
}
