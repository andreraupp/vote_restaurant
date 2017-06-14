package br.com.andreraupp.voterestaurant.models;

/**
 * Created by andre on 11/06/2017.
 */

public class Vote implements Comparable<Vote> {
    private String date;
    private String restaurantId;
    private String restaurantName;
    private String userId;

    public Vote() {

    }

    public Vote(String date, String restaurantId, String restaurantName, String userId) {
        this.date = date;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(Vote vote) {
        return this.getRestaurantName().compareTo(vote.getRestaurantName());
    }
}
