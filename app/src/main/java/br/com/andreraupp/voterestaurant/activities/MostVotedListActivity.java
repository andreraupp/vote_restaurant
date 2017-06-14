package br.com.andreraupp.voterestaurant.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.andreraupp.voterestaurant.R;
import br.com.andreraupp.voterestaurant.adapters.MostVotedListAdapter;
import br.com.andreraupp.voterestaurant.business.VoteBusiness;
import br.com.andreraupp.voterestaurant.models.MostVoted;
import br.com.andreraupp.voterestaurant.models.Vote;

/**
 * List most voted restaurants previous day
 */
public class MostVotedListActivity extends AppCompatActivity {
    static final String CHILD_VOTE = "vote";

    private ProgressBar spinner;
    private List<MostVoted> mostVoteds = new ArrayList<MostVoted>();
    final List<Vote> votes = new ArrayList<Vote>();
    private ListView listView;
    private TextView titleText;
    private MostVotedListAdapter mostVotedListAdapter;
    private DatabaseReference databaseReference;
    private String date;
    private VoteBusiness voteBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        voteBusiness = new VoteBusiness();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_most_voted_list);
        titleText = (TextView) findViewById(R.id.title);
        listView = (ListView) findViewById(R.id.list);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date myDate = voteBusiness.getYesterday();
        date = dateFormat.format(myDate);
        titleText.setText(titleText.getText() + date);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(CHILD_VOTE).orderByChild("date").equalTo(voteBusiness.getYesterdayString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                votes.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Vote vote = child.getValue(Vote.class);
                    if (vote.getUserId() != null)
                        votes.add(vote);
                }
                setListAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void refreshVotes(List<Vote> refreshVotes) {
        try {
            MostVoted mostVoted = null;
            List<Vote> votesAux = refreshVotes;
            for (Vote vote : refreshVotes) {
                if (mostVoted == null || !mostVoted.getRestaurantId().equals(vote.getRestaurantId())) {
                    mostVoted = new MostVoted(vote.getRestaurantId(), vote.getRestaurantName(), 0);
                    for (Vote voteAux : votesAux) {
                        if (voteAux.getRestaurantId().equals(vote.getRestaurantId())) {
                            mostVoted.setVotes(mostVoted.getVotes() + 1);
                        }
                    }
                    mostVoteds.add(mostVoted);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void setListAdapter() {
        if (!this.votes.isEmpty() && !this.votes.isEmpty()) {
            Collections.sort(this.votes);
            refreshVotes(this.votes);
        }

        Collections.sort(this.mostVoteds);
        mostVotedListAdapter = new MostVotedListAdapter(this.mostVoteds, this);
        listView.setAdapter(mostVotedListAdapter);
        spinner.setVisibility(View.GONE);
    }
}