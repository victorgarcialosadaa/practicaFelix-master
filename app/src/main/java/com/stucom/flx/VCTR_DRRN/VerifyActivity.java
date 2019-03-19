package com.stucom.flx.VCTR_DRRN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;

public class VerifyActivity extends AppCompatActivity {
    Prefs prefs;
    EditText verifyField;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        verifyField = findViewById(R.id.fieldVerify);
        button = findViewById(R.id.btn_verify);

        final String emailField = getIntent().getExtras().getString("email");
        /*Una vez que lo haya clicado ejecutará lo siguiente*/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Esto nos ayudará a comprobar si el código introducido es correcto. Si lo es nos enviará al menu principal*/
                String URL = "https://api.flx.cat/dam2game/register";
                StringRequest request = new StringRequest
                        (Request.Method.POST, URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String json = response;
                                        Gson gson = new Gson();
                                        Type typeToken = new TypeToken<APIResponse<String>>() {
                                        }.getType();
                                        APIResponse<String> apiResponse = gson.fromJson(json, typeToken);
                                        String userToken = apiResponse.getData();
                                        //Guardo el token dentro de la instancia prefs
                                        Prefs.getInstance(VerifyActivity.this).setToken(userToken);
                                        //Creamos una instancia de jugador gracias a la api de felix.
                                        newPlayer();

                                        /*Si la instancia del token del jugador no es vacia mandará al menú principal. De lo contrario se irá a registrar otra vez*/
                                        if (!Prefs.getInstance(VerifyActivity.this).getToken().equalsIgnoreCase("")) {
                                            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                           /* Intent intent = new Intent(VerifyActivity.this, RegisterActivity.class);
                                            startActivity(intent);*/
                                            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }

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
                        params.put("email", emailField);
                        params.put("verify", verifyField.getText().toString());
                        return params;
                    }
                };
                MyVolley.getInstance(VerifyActivity.this).add(request);
            }


        });
    }

    public void newPlayer() {
        String URL = "https://api.flx.cat/dam2game/user?token=" + Prefs.getInstance(VerifyActivity.this).getToken();
        StringRequest request = new StringRequest
                (Request.Method.GET, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String json = response;
                                Gson gson = new Gson();
                                Type typeToken = new TypeToken<APIResponse<Player>>() {
                                }.getType();
                                APIResponse<Player> apiResponse = gson.fromJson(json, typeToken);
                                Player player = apiResponse.getData();
                                /*Asignamos los atributos del jugador que cogemos de la api*/
                                player.setId(apiResponse.getData().getId());
                                player.setName(apiResponse.getData().getName());
                                player.setImage(apiResponse.getData().getImage());
                                player.setFrom(apiResponse.getData().getFrom());
                                player.setTotalScore(apiResponse.getData().getTotalScore());
                                player.setLastLevel(apiResponse.getData().getLastLevel());
                                player.setLastScore(apiResponse.getData().getLastScore());
                                player.setScores(apiResponse.getData().getScores());

                                Prefs.getInstance(VerifyActivity.this).setPlayer(player);


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
        };
        MyVolley.getInstance(VerifyActivity.this).add(request);
    }


}
