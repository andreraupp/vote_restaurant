package br.com.andreraupp.voterestaurant.models;

/**
 * Created by andre on 12/06/2017.
 */

public class Winner {
    private String date;
    private String restaurantId;
    private String restaurantName;

    public Winner() {

    }

    public Winner(String date, String restaurantId, String restaurantName) {
        this.date = date;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getDate() {
        if (date != null) {
            return date;
        }

        return "";
    }

    public void setData(String date) {
        this.date = date;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
