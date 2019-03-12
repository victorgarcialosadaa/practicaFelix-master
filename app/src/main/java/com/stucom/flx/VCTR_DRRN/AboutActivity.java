package com.stucom.flx.VCTR_DRRN;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Button btnWhereAreWe = findViewById(R.id.btnWhereAreWe);
        btnWhereAreWe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("geo:41.3857257,2.1651803?z=18&q=Stucom");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }
}
