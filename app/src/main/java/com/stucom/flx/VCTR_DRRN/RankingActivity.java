package com.stucom.flx.VCTR_DRRN;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.flx.VCTR_DRRN.api.APIResponse;
import com.stucom.flx.VCTR_DRRN.model.MyVolley;
import com.stucom.flx.VCTR_DRRN.model.Player;
import com.stucom.flx.VCTR_DRRN.model.Prefs;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    TextView textView;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadPlayers();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        downloadPlayers();
    }

    final String URL = "https://api.flx.cat/dam2game/ranking?token="+ Prefs.getInstance(RankingActivity.this).getToken();
    public void downloadPlayers() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String json = response.toString();
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<Player>>>() {}.getType();
                        APIResponse<List<Player>> apiResponse = gson.fromJson(json, typeToken);
                        List<Player> players = apiResponse.getData();

                        PlayersAdapter adapter = new PlayersAdapter(players);
                        recyclerView.setAdapter(adapter);

                        String message = "Downloaded " + players.size() + " players\n";
                        for (Player player : players) {
                            message += player.getImage() + player.getName() +  "\n"
                            + "score: "+ player.getLastScore();
                        }
                        //textView.setText(message);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.toString();
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            message = response.statusCode + " " + message;
                        }
                        textView.setText("ERROR " + message);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        MyVolley.getInstance(this).add(request);
    }


    class PlayersViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textViewScore;
        ImageView imageView;

        PlayersViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.rankingTextView);
            textViewScore = itemView.findViewById(R.id.rankingTextViewScore);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    class PlayersAdapter extends RecyclerView.Adapter<PlayersViewHolder> {

        private List<Player> players;

        PlayersAdapter(List<Player> players) {
            super();
            this.players = players;
        }

        @NonNull @Override
        public PlayersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_players, parent, false);
            return new PlayersViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull PlayersViewHolder viewHolder, int position) {
            Player player = players.get(position);
            player.getImageAPI();
            viewHolder.textView.setText(player.getName());
            viewHolder.textViewScore.setText(player.getLastScore());
            Picasso.get().load(player.getImage()).into(viewHolder.imageView);
        }
        @Override
        public int getItemCount() {
            return players.size();
        }
    }


}
