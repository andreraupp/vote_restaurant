package br.com.andreraupp.voterestaurant.models;

/**
 * Created by andre on 13/06/2017.
 */

public class MostVoted implements Comparable<MostVoted> {
    private String restaurantId;
    private String restaurantName;
    private Integer votes;

    public MostVoted() {

    }

    public MostVoted(String restaurantId, String restaurantName, Integer votes) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.votes = votes;
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

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    @Override
    public int compareTo(MostVoted mostVoted) {
        int order = 1;
        if (this.getVotes() > mostVoted.getVotes()) {
            order = -1;
        }

        return order;
    }
}
