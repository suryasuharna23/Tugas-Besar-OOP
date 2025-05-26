package spakborhills.cooking;

public class ActiveCookingProcess {
    public String foodNameToProduce;
    public int foodQuantityToProduce;
    public int gameDayFinish;
    public int gameHourFinish;
    public int gameMinuteFinish;

    public ActiveCookingProcess(String foodName, int quantity, int finishDay, int finishHour, int finishMinute) {
        this.foodNameToProduce = foodName;
        this.foodQuantityToProduce = quantity;
        this.gameDayFinish = finishDay;
        this.gameHourFinish = finishHour;
        this.gameMinuteFinish = finishMinute;
    }
}