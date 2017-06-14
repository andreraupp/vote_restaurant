package br.com.andreraupp.voterestaurant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by andre on 10/06/2017.
 */

public class ResultResponse implements Serializable {
    List<Restaurant> results;

    public List<Restaurant> getRestaurants() {
        return this.results;
    }
}
