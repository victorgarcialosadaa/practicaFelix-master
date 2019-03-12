package com.stucom.flx.VCTR_DRRN;

import android.content.Context;
import android.content.Intent;
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
import com.stucom.flx.VCTR_DRRN.model.MyVolley;
import com.stucom.flx.VCTR_DRRN.model.Prefs;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        emailField = findViewById(R.id.fieldRegister);
        Button btn_register = findViewById(R.id.btn_Register);

        /*Cuando cique al boton que ejecute el metodo onRegister*/
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister(v);
            }
        });
    }

    /*Método que registra al usuario a través de su email y guardamos su email para  así en la siguiente vista aprovecharlo. Nos enviará via email el código para introducirlo más tarde.*/
    public void onRegister(View v) {
        String URL = "https://api.flx.cat/dam2game/register";
        StringRequest request = new StringRequest
                (Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                /*si ha salido bien, iremos a la vista de verificar donde colocaremos el código pasando el email del usuario*/
                                Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                                intent.putExtra("email", emailField.getText().toString());
                                startActivity(intent);
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
                params.put("email", emailField.getText().toString());
                return params;
            }
        };
        MyVolley.getInstance(RegisterActivity.this).add(request);
    }
}
