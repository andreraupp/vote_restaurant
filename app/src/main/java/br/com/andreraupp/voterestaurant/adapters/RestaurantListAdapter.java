package br.com.andreraupp.voterestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.andreraupp.voterestaurant.R;
import br.com.andreraupp.voterestaurant.models.Restaurant;

/**
 * Created by andre on 11/06/2017.
 */

public class RestaurantListAdapter extends BaseAdapter {

    private List<Restaurant> restaurants;
    private Context context;

    public RestaurantListAdapter(List<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return this.restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        RestaurantViewHolder viewHolder = new RestaurantViewHolder();
        Restaurant restaurant = this.restaurants.get(position);

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.list_restaurants, null);
            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.vicinity = (TextView) view.findViewById(R.id.vicinity);
            viewHolder.votes = (TextView) view.findViewById(R.id.votes);
            view.setTag(viewHolder);
        } else {
            viewHolder = (RestaurantViewHolder) view.getTag();
        }

        if (restaurant.getMyVote()) {
            view.setBackgroundColor(context.getResources().getColor(R.color.lightGrey));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        viewHolder.name.setText(restaurant.getName());
        viewHolder.vicinity.setText(restaurant.getVicinity());
        viewHolder.votes.setText(restaurant.getVotes().toString());

        return view;
    }

    static class RestaurantViewHolder {
        TextView name;
        TextView vicinity;
        TextView votes;
    }
}
