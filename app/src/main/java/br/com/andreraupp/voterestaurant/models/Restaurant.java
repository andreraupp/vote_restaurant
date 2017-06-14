package br.com.andreraupp.voterestaurant.models;

import java.io.Serializable;

/**
 * Created by andre on 10/06/2017.
 */

public class Restaurant implements Serializable, Comparable<Restaurant> {
    private String id;
    private String name;
    private String vicinity;
    private Integer votes = 0;
    private Boolean myVote = false;
    private Boolean winnerWeek = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Integer getVotes() {
        return this.votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Boolean getMyVote() {
        return this.myVote;
    }

    public void setMyVote(Boolean myVote) {
        this.myVote = myVote;
    }

    public Boolean getWinnerWeek() {
        return this.winnerWeek;
    }

    public void setWinnerWeek(Boolean winnerWeek) {
        this.winnerWeek = winnerWeek;
    }


    @Override
    public int compareTo(Restaurant restaurant) {
        int order = 1;
        if (this.getVotes() > restaurant.getVotes()) {
            order = -1;
        }

        return order;
    }
}
