package com.stucom.flx.VCTR_DRRN;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.flx.VCTR_DRRN.api.APIResponse;
import com.stucom.flx.VCTR_DRRN.model.MyVolley;
import com.stucom.flx.VCTR_DRRN.model.Player;
import com.stucom.flx.VCTR_DRRN.model.Prefs;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int[] BUTTONS_ID = new int[] {
      R.id.btnPlay, R.id.btnRanking, R.id.btnSettings, R.id.btnAbout
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Prefs.getInstance(this);
        setContentView(R.layout.activity_main);
        for(int id: BUTTONS_ID) {
            Button button = findViewById(id);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch(view.getId()) {
            case R.id.btnPlay:
                intent = new Intent(MainActivity.this, NewPlayActivity.class);
                break;
            case R.id.btnRanking:
                intent = new Intent(MainActivity.this, RankingActivity.class);
                break;
            case R.id.btnSettings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.btnAbout:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                break;
        }
        if (intent == null) return;
        startActivity(intent);
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
