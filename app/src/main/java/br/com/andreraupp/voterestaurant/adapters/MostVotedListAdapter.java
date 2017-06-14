package br.com.andreraupp.voterestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.andreraupp.voterestaurant.R;
import br.com.andreraupp.voterestaurant.models.MostVoted;

/**
 * Created by andre on 13/06/2017.
 */

public class MostVotedListAdapter extends BaseAdapter {

    private List<MostVoted> mostVoteds;
    private Context context;

    public MostVotedListAdapter(List<MostVoted> mostVoteds, Context context) {
        this.mostVoteds = mostVoteds;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.mostVoteds.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mostVoteds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MostVotedViewHolder viewHolder = new MostVotedViewHolder();
        MostVoted mostVoted = this.mostVoteds.get(position);

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.list_most_voted, null);
            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.votes = (TextView) view.findViewById(R.id.votes);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MostVotedViewHolder) view.getTag();
        }

        viewHolder.name.setText(mostVoted.getRestaurantName());
        viewHolder.votes.setText(mostVoted.getVotes().toString());

        return view;
    }

    static class MostVotedViewHolder {
        TextView name;
        TextView votes;
    }
}
