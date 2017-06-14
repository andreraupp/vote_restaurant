package br.com.andreraupp.voterestaurant.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.andreraupp.voterestaurant.R;
import br.com.andreraupp.voterestaurant.adapters.RestaurantListAdapter;
import br.com.andreraupp.voterestaurant.business.VoteBusiness;
import br.com.andreraupp.voterestaurant.models.Restaurant;
import br.com.andreraupp.voterestaurant.models.Vote;
import br.com.andreraupp.voterestaurant.models.Winner;
import br.com.andreraupp.voterestaurant.parsers.ParseRestaurant;

import static br.com.andreraupp.voterestaurant.business.VoteBusiness.hasPermissions;

/**
 * List nearby restaurants for voting
 */
public class RestaurantsListActivity extends AppCompatActivity {
    static final String CHILD_VOTE = "vote";
    static final String CHILD_WINNER = "winners";
    static final int PERMISSION_ALL = 1;

    private ProgressBar spinner;
    private List<Restaurant> restaurants;
    private List<Winner> weekWinners = new ArrayList<Winner>();
    private View divider;
    final List<Vote> votes = new ArrayList<Vote>();
    private Button mostVotedButton;
    private ListView listView;
    private TextView titleText;
    private LinearLayout winnerLayout;
    private TextView winnerName;
    private RestaurantListAdapter restaurantListAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Winner winnerRestaurant;
    private boolean votedToday = false;
    private VoteBusiness votedBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        votedBusiness = new VoteBusiness();
        setContentView(R.layout.activity_restaurants_list);
        divider = (View) findViewById(R.id.divider);
        winnerLayout = (LinearLayout) findViewById(R.id.layout_winner);
        winnerName = (TextView) findViewById(R.id.restaurant_name);
        titleText = (TextView) findViewById(R.id.title);
        mostVotedButton = (Button) findViewById(R.id.most_voted);
        winnerLayout.setVisibility(View.GONE);
        titleText.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.list);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        mostVotedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMostVoted();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (closedVotation()) {
                    Toast.makeText(RestaurantsListActivity.this, getString(R.string.close_votation), Toast.LENGTH_LONG).show();
                } else if (votedToday) {
                    Toast.makeText(RestaurantsListActivity.this, getString(R.string.voted_today), Toast.LENGTH_LONG).show();
                } else {
                    final Restaurant restaurant = restaurants.get(position);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantsListActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.your_vote));
                    alertDialogBuilder
                            .setMessage(getString(R.string.you_want_vote_this_restaurante))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    setVoteCloud(restaurant);
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        getPermissions();
        callAsynchronousTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onClickMostVoted() {
        Intent intent = new Intent(this, MostVotedListActivity.class);
        startActivity(intent);
    }

    private void getPermissions() {
        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_NETWORK_STATE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            enableLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocationService();
                } else {}
                return;
            }
        }
    }

    public void enableLocationService() {
        LocationManager lManager = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                StringBuilder sbValue = new StringBuilder(votedBusiness.getUrlNearbySearch(locat.getLatitude(), locat.getLongitude()));
                RestaurantsListTask placesTask = new RestaurantsListTask();
                placesTask.execute(sbValue.toString());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, lListener);
                lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, lListener);
            }
        }
    }

    private void refreshVotes(List<Vote> refreshVotes) {
        clearVotes();
        boolean myVote = false;

        try {
            for (Vote vote : refreshVotes) {
                if (vote.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                    myVote = true;
                    votedToday = true;
                }
                for (Restaurant restaurante : restaurants) {
                    if (restaurante.getId().equals(vote.getRestaurantId())) {
                        restaurante.setVotes(restaurante.getVotes() + 1);
                        if (myVote) {
                            restaurante.setMyVote(true);
                            myVote = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void clearVotes() {
        for (Restaurant restaurante : restaurants) {
            if (restaurante.getVotes() > 0) {
                restaurante.setVotes(0);
            }
        }
    }

    public boolean closedVotation() {
        boolean closed = false;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 45);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date timeLimit = cal.getTime();

        if (new Date().getTime() >= timeLimit.getTime()) {
            closed = true;
        }

        if (closed && this.restaurants != null && this.restaurants.get(0).getVotes() >= 1) {
            if (winnerRestaurant == null) {
                winnerRestaurant = new Winner(votedBusiness.getToday(), this.restaurants.get(0).getId(), this.restaurants.get(0).getName());
            }
            titleText.setVisibility(View.GONE);
            winnerName.setText(winnerRestaurant.getRestaurantName());
            winnerLayout.setVisibility(View.VISIBLE);
            getWinnerCloud();
        } else {
            titleText.setVisibility(View.VISIBLE);
            winnerLayout.setVisibility(View.GONE);
        }

        return closed;
    }

    private void setListAdapter() {
        if (!this.votes.isEmpty() && !this.votes.isEmpty()) {
            refreshVotes(this.votes);
        }

        Collections.sort(this.restaurants);
        restaurantListAdapter = new RestaurantListAdapter(this.restaurants, this);
        listView.setAdapter(restaurantListAdapter);
        divider.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    public void setWinnerCloud(Winner winner) {
        try {
            databaseReference.child(CHILD_WINNER).push().setValue(winner);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void getWinnerCloud() {
        databaseReference.child(CHILD_WINNER).orderByChild("date").equalTo(votedBusiness.getToday()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                if (children.iterator().hasNext()) {
                    for (DataSnapshot child : children) {
                        winnerRestaurant = child.getValue(Winner.class);
                        titleText.setVisibility(View.GONE);
                        winnerName.setText(winnerRestaurant.getRestaurantName());
                        winnerLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (winnerRestaurant != null) {
                        setWinnerCloud(winnerRestaurant);
                    }
                }
                setListAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setVoteCloud(Restaurant selectedRestaurant) {
        Vote vote = new Vote(votedBusiness.getToday(), selectedRestaurant.getId(), selectedRestaurant.getName(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.child(CHILD_VOTE).push().setValue(vote);
    }

    public void getVotesCloud() {
        databaseReference.child(CHILD_VOTE).orderByChild("date").equalTo(votedBusiness.getToday()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        databaseReference.child(CHILD_VOTE).orderByChild("date").equalTo(votedBusiness.getToday()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                boolean add = true;
                Vote addVote = dataSnapshot.getValue(Vote.class);
                if (votes.size() >= 1) {
                    for (Vote vote : votes) {
                        if (vote.getDate().equals(addVote.getDate()) && vote.getRestaurantId().equals(addVote.getRestaurantId()) && vote.getUserId().equals(addVote.getUserId())) {
                            add = false;
                        }
                    }
                    if (add)
                        votes.add(addVote);
                } else {
                    votes.add(addVote);
                }
                setListAdapter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getWeekWinnersCloud() {
        databaseReference.child(CHILD_WINNER).orderByChild("date").startAt(votedBusiness.getDateStartWeek()).endAt(votedBusiness.getYesterdayString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                weekWinners.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Winner winner = child.getValue(Winner.class);
                    if (winner.getRestaurantId() != null)
                        weekWinners.add(winner);
                }
                removeWeekWinners();
                setListAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void removeWeekWinners() {
        List<Restaurant> restarantsAux =  new ArrayList<>(restaurants);
        for (Winner winner : weekWinners) {
            for (Restaurant restaurante : restarantsAux) {
                if (restaurante.getId().equals(winner.getRestaurantId())) {
                    restaurants.remove(restaurante);
                }
            }
        }
    }

    private class RestaurantsListTask extends AsyncTask<String, Integer, List<Restaurant>> {
        String data = null;

        private String nearbySearch(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("While downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected List<Restaurant> doInBackground(String... url) {
            try {
                data = nearbySearch(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            List<Restaurant> places = null;
            ParseRestaurant placeJson = new ParseRestaurant();

            try {
                places = placeJson.parse(data);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<Restaurant> list) {
            Log.d("Restaurants List", "list size: " + list.size());
            restaurants = list;
            getVotesCloud();
            getWinnerCloud();
            getWeekWinnersCloud();
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (votes != null && votes.size() >= 1)
                            closedVotation();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 300000);
    }
}