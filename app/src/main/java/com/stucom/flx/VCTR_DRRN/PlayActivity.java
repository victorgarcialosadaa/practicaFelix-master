package com.stucom.flx.VCTR_DRRN;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stucom.flx.VCTR_DRRN.model.MyVolley;
import com.stucom.flx.VCTR_DRRN.model.Player;
import com.stucom.flx.VCTR_DRRN.model.Prefs;

import java.util.HashMap;
import java.util.Map;

public class PlayActivity extends AppCompatActivity {
    EditText edScore, edLevel;
    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        edScore = findViewById(R.id.fieldsScore);
        edLevel = findViewById(R.id.fieldLevel);
        player = Prefs.getInstance(PlayActivity.this).getPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load player info from SharedPrefs
        edScore.setText(player.getTotalScore());
        edLevel.setText(player.getLastLevel());
    }

    @Override
    public void onPause() {
        updateScore();
        // Save player info from SharedPrefs (save changes on name and email only)
        player.setTotalScore(edScore.getText().toString());
        player.setLastLevel(edLevel.getText().toString());
        super.onPause();
    }

    public void updateScore() {
        final String scoreField = edScore.getText().toString();
        final String levelField = edLevel.getText().toString();

        String URL = "https://api.flx.cat/dam2game/user/score?token=" + Prefs.getInstance(PlayActivity.this).getToken();
        StringRequest request = new StringRequest
                (Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.toString();
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            Context context = getApplicationContext();
                            CharSequence text = response.statusCode + " " + message;
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("level", levelField);
                params.put("score", scoreField);

                return params;
            }
        };
        MyVolley.getInstance(PlayActivity.this).add(request);
    }


}
