package br.com.andreraupp.voterestaurant.parsers;

import com.google.gson.Gson;
import java.util.List;
import br.com.andreraupp.voterestaurant.models.Restaurant;
import br.com.andreraupp.voterestaurant.models.ResultResponse;

/**
 * Created by andre on 10/06/2017.
 */

public class ParseRestaurant {
    public List<Restaurant> parse(String response) {
        Gson gson = new Gson();
        ResultResponse resultResponse = gson.fromJson(response, ResultResponse.class);
        return resultResponse.getRestaurants();
    }
}
